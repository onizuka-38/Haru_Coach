package com.harucoach.harucoachfront.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harucoach.harucoachfront.data.models.AnswerItem
import com.harucoach.harucoachfront.data.models.Question
import com.harucoach.harucoachfront.data.models.ResultResponse
import com.harucoach.harucoachfront.data.repository.CognitiveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CognitiveUiState(
    val loading: Boolean = false,
    val sessionId: Long? = null,
    val questions: List<Question> = emptyList(),
    val answers: Map<Int, String> = emptyMap(),
    val result: ResultResponse? = null,
    val error: String? = null
)

@HiltViewModel
class CognitiveViewModel @Inject constructor(
    private val repo: CognitiveRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CognitiveUiState())
    val uiState: StateFlow<CognitiveUiState> = _uiState

    // init에서 자동 시작 제거
    // 사용자가 버튼을 눌러야만 startTest()가 호출
    fun startTest(userId: Int = 2, count: Int = 10, category: String? = null) {
        _uiState.value = _uiState.value.copy(loading = true, error = null, result = null)
        viewModelScope.launch {
            try {
                val res = repo.startTest(userId, count, category)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    sessionId = res.sessionId,
                    questions = res.questions,
                    answers = emptyMap()
                )
                Log.d("CognitiveViewModel", "검사 시작 성공! sessionId: ${res.sessionId}, 문제 수: ${res.questions.size}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "시작 실패: ${e.message}"
                )
                Log.e("CognitiveViewModel", "검사 시작 실패", e)
            }
        }
    }

    fun updateAnswer(questionNo: Int, text: String) {
        val newMap = _uiState.value.answers.toMutableMap()
        newMap[questionNo] = text
        _uiState.value = _uiState.value.copy(answers = newMap)
    }

    fun submit(sttList: List<String>, latencyMs: List<Int>) {
        val state = _uiState.value
        val sid = state.sessionId ?: run {
            Log.e("CognitiveViewModel", "sessionId가 없어서 제출 불가!")
            return
        }

        val payload = state.questions.mapIndexed { index, q ->
            AnswerItem(
                questionNo = q.questionNo,
                questionId = q.questionId,
                sttText = sttList.getOrNull(index) ?: "",  // 안전하게 가져오기
                typedText = state.answers[q.questionNo] ?: "",
                latencyMs = latencyMs.getOrNull(index) ?: 0
            )
        }

        Log.d("CognitiveViewModel", "제출 데이터: $payload")

        _uiState.value = state.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val res = repo.submitAnswers(sid, payload)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    result = res
                )
                Log.d("CognitiveViewModel", "제출 성공! 총점: ${res.totalScore}, 등급: ${res.grade}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "제출 실패: ${e.message}"
                )
                Log.e("CognitiveViewModel", "제출 실패", e)
            }
        }
    }

    fun reset() {
        _uiState.value = CognitiveUiState()
        Log.d("CognitiveViewModel", "상태 초기화 완료")
    }
}