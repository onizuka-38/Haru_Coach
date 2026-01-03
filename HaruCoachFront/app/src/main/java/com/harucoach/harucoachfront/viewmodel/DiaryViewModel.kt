package com.harucoach.harucoachfront.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel  // ViewModel은 앱의 뇌처럼, 화면이 바뀌어도 기억을 지켜줘요.
import androidx.lifecycle.viewModelScope  // 이건 안전한 방에서 일을 하게 해줘요, 앱이 안 깨지게.
import com.harucoach.harucoachfront.data.models.DiaryAiEntry
import com.harucoach.harucoachfront.data.models.DiaryEntry  // 일기 조각(날짜, 기분, 텍스트)을 가져오는 친구예요.
import com.harucoach.harucoachfront.data.models.DiaryResponse
import com.harucoach.harucoachfront.data.models.ResultAiDiary
import com.harucoach.harucoachfront.data.repository.DiaryRepository  // 데이터 창고(저장소)를 연결해줘요, 백엔드나 메모리에서 일기를 가져와요.
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow  // 변하는 값을 지켜보는 창문예요, 값이 바뀌면 알려줘요.
import kotlinx.coroutines.flow.StateFlow  // 안전하게 지켜보는 창문, 밖에서 볼 수만 있어요.
import kotlinx.coroutines.flow.asStateFlow  // Mutable을 State로 바꿔주는 마법, 안전하게 만들어요.
import kotlinx.coroutines.launch  // 천천히 일하는 로봇을 부르는 주문, 기다리면서 일해요.
import java.time.LocalDate  // 날짜를 다루는 도구, 오늘 날짜를 알 수 있어요.
import java.time.format.DateTimeFormatter  // 날짜를 "2025-11-09"처럼 예쁘게 바꿔주는 펜예요.
import javax.inject.Inject
import kotlin.collections.mapKeys
import kotlin.collections.mapValues
import kotlin.collections.set

// 화면 상태를 나타내는 sealed class (간단한 상태 모음)
// - Idle: 아무 일 없는 상태
// - Loading: 데이터를 불러오는 중
// - Saving: 저장하는 중
// - Saved: 저장이 끝난 상태
// - Error: 무언가 잘못된 상태, 오류 메시지 포함
sealed class DiaryUiState {  // end sealed DiaryUiState
    object Idle : DiaryUiState()
    object Loading : DiaryUiState()
    object Saving : DiaryUiState()
    object Saved : DiaryUiState()
    data class Error(val message: String) : DiaryUiState()
} // end DiaryUiState

/**
 * DiaryViewModel
 * 일기 관련 사항들 불러와서 화면에 보여줄때, 화면에서 저장, 취소 등의 버튼을 눌렀을때 등 화면을 다시 rerender 할때 동작들
 */
@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository // 데이터 공급자(임시 또는 실제 서버)
) : ViewModel() { // start DiaryViewModel

    // 날짜 문자열 포맷터: "yyyy-MM-dd" 형태로 변환할 때 사용
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE // end dateFormatter

    // ---------- UI 상태 관리용 StateFlow들 ----------
    private val _uiState = MutableStateFlow<DiaryUiState>(DiaryUiState.Idle) // 내부 쓰기용
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow() // 외부 읽기용
    // end uiState
    // ---------- UI 상태 관리용 StateFlow들 ----------
    private val _uiStateAi = MutableStateFlow<DiaryUiState>(DiaryUiState.Idle) // 내부 쓰기용
    val uiStateAi: StateFlow<DiaryUiState> = _uiStateAi.asStateFlow() // 외부 읽기용
    // end uiState

    private val _selectedDate = MutableStateFlow(LocalDate.now()) // 사용자가 선택한 날짜 (초기: 오늘)
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    // end selectedDate

    private val _currentText = MutableStateFlow("") // 일기 텍스트의 현재 값
    val currentText: StateFlow<String> = _currentText.asStateFlow()
    // end currentText

    private val _currentMood = MutableStateFlow("보통") // 현재 선택된 감정(이모지 + 설명)
    val currentMood: StateFlow<String> = _currentMood.asStateFlow()
    // end currentMood

    // 기존 일기 여부를 알려주는 상태
    private val _isExistingEntry = MutableStateFlow(false)
    // end isExistingEntry

    // 메모리 저장소: 키는 "yyyy-MM-dd" 문자열 -> 값은 DiaryEntry
    // 실제 서비스에서는 Room/DB 또는 Repository 캐시를 사용합니다.
    private val memoryStore: MutableMap<String, DiaryEntry> = mutableMapOf() // end memoryStore

    /**
     * ---------- 초기화: 앱 시작 시 Repository에서 전체 일기를 불러옴 ----------
     * init 은 화면이 띄워질때 실행되는 함수 => 정확히는 viewmodel 이 띄워질때
     */
    init {
        viewModelScope.launch {
            _uiState.value = DiaryUiState.Loading // 로딩중 상태 표시
            try {
                // DiaryRepository 에서 값 가져옴
                val fetched = repository.fetchAllDiaries()
                memoryStore.clear()
                memoryStore.putAll(fetched) // 메모리 저장소에 채움
                refreshMoodMap()   // ← 추가: UI에 변경 알림
                _uiState.value = DiaryUiState.Idle // 로딩 끝
            } catch (e: Exception) {
                // 실패하면 에러 상태로 변경
                _uiState.value = DiaryUiState.Error(e.message ?: "초기 데이터 로드 실패")
            }
        }
    } // end init

    // ---------- 날짜 선택 처리 ----------
    /**
     * 사용자가 달력에서 날짜를 누르면 이 함수가 호출됩니다.
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date // 선택한 날짜를 저장
        loadDiaryFor(date) // 해당 날짜의 일기를 불러와 UI에 반영
        _isExistingEntry.value = memoryStore.containsKey(date.format(dateFormatter)) // 기존 데이터 존재여부 갱신
    } // end selectDate

    // ---------- 텍스트/기분 업데이트 헬퍼 ----------

    /**
     * 텍스트 동작 UI update
     */
    fun updateText(text: String) {
        _currentText.value = text
    } // end updateText

    /**
     * 기분 동작 UI update
     */
    fun updateMood(mood: String) {
        _currentMood.value = mood
    } // end updateMood

    /**
     * ---------- 저장(새 일기 또는 수정) ----------
     *  사용자가 저장 버튼을 누르면 이 함수가 호출됩니다.
     *  TODO update 따로 구현
     */
    fun saveDiary() {

        val date = _selectedDate.value
        val key = date.format(dateFormatter)
        val entry = DiaryEntry(entry_date = date.toString(), mood_code = _currentMood.value, content = _currentText.value)

        viewModelScope.launch {
            _uiState.value = DiaryUiState.Saving // 저장 중 상태
            try {
                // 시뮬레이션: 실제 서버 호출이 있는 경우에는 repository가 네트워크 통신을 담당
                delay(500)

                if (_isExistingEntry.value) {
                    // 기존에 있으면 업데이트 호출
                    repository.updateDiary(entry)
                } else {
                    // 없으면 새로 저장
                    //repository.saveDiary(entry)
                    repository.startDiaryTest(entry)
                }

                // 메모리에도 반영해서 즉시 UI에 갱신되도록 함
                memoryStore[key] = entry

                // refresh => to UI
                refreshMoodMap()

                // 기존 여부 상태를 true로 바꿈 (이제 이 날짜는 저장된 상태)
                _isExistingEntry.value = true

                // 성공 상태 표시
                _uiState.value = DiaryUiState.Saved
                delay(600) // 사용자 피드백을 위한 짧은 지연
                _uiState.value = DiaryUiState.Idle
            } catch (e: Exception) {
                // 실패 시 에러 상태로 변환
                _uiState.value = DiaryUiState.Error(e.localizedMessage ?: "저장 실패")
            }
        }
    } // end saveDiary

    /**
     * 특정 일기 날짜 불러오기
     */
    private fun loadDiaryFor(date: LocalDate) {
        val key = date.format(dateFormatter)
        viewModelScope.launch {
            _uiState.value = DiaryUiState.Loading
            try {
                // 시뮬레이션 대기
                delay(200)

                // 메모리에서 찾음
                val entry = memoryStore[key]
                if (entry != null) {
                    // 있으면 UI 상태를 해당 값으로 채움
                    _currentMood.value = entry.mood_code
                    _currentText.value = entry.content
                } else {
                    // 없으면 기본값으로 초기화
                    _currentMood.value = "보통"
                    _currentText.value = ""
                }

                // 기존 여부도 갱신
                _isExistingEntry.value = entry != null

                _uiState.value = DiaryUiState.Idle
            } catch (e: Exception) {
                _uiState.value = DiaryUiState.Error(e.localizedMessage ?: "불러오기 실패")
            }
        }// end viewModelScope.launch
    } // end loadDiaryFor


    // TODO 현재 이모지로 되어있으나 이미지로 변경 될지, 너무 올래 걸릴꺼같으면 skip
    // 새로 추가: Compose/UI에서 관찰할 수 있는 Map<LocalDate, String>
    private val _moodMap = MutableStateFlow<Map<LocalDate, String>>(emptyMap())
    val moodMap: StateFlow<Map<LocalDate, String>> = _moodMap.asStateFlow()

    // 헬퍼: memoryStore -> LocalDate->mood Map 변환
    private fun refreshMoodMap() {
        // memoryStore의 키는 "yyyy-MM-dd" 문자열임을 가정
        val map = memoryStore.mapKeys { LocalDate.parse(it.key, dateFormatter) }
            .mapValues { it.value.mood_code }
        _moodMap.value = map
    }

    private val _aiResult = MutableStateFlow<ResultAiDiary?>(null)
    val aiResult: StateFlow<ResultAiDiary?> = _aiResult.asStateFlow()


    fun DiaryAi() {
        val date = _selectedDate.value
        val entry = DiaryAiEntry(entry_date = date.toString(), display_name ="쿠모", content = _currentText.value)

        viewModelScope.launch {
            _uiStateAi.value = DiaryUiState.Saving // 저장 중 상태
            try {
                delay(500) // 시뮬레이션 (실제 API 호출에서는 필요 없을 수 있음)
                val result = repository.startDiaryAi(entry) // repository에서 결과 받기
                _aiResult.value = result // AI 분석 결과 StateFlow 업데이트

                _uiStateAi.value = DiaryUiState.Saved // 성공 상태 표시
                delay(600) // 사용자 피드백을 위한 짧은 지연
                _uiStateAi.value = DiaryUiState.Idle
            } catch (e: Exception) {
                _uiStateAi.value = DiaryUiState.Error(e.localizedMessage ?: "ai 실패")
                _aiResult.value = null // 실패 시 결과 초기화
                Log.e("DiaryViewModel", "DiaryAi failed: ${e.localizedMessage}", e) // 에러 로깅 추가
            }
        }
    }

    // --- AI 결과 초기화를 위한 함수 추가 ---
    fun clearAiResult() {
        _aiResult.value = null
    }
} // end DiaryViewModel