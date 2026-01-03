package com.harucoach.harucoachfront.data.repository

import com.harucoach.harucoachfront.data.models.DiaryAiEntry
import com.harucoach.harucoachfront.data.models.DiaryEntry
import com.harucoach.harucoachfront.data.models.DiaryListResponse
import com.harucoach.harucoachfront.data.models.DiaryResponse
import com.harucoach.harucoachfront.data.models.ResultAiDiary
import com.harucoach.harucoachfront.data.models.ResultDiary
import com.harucoach.harucoachfront.data.remote.ApiService  // 기존 ApiService import
import com.harucoach.harucoachfront.data.remote.RetrofitInstance.api
import java.time.LocalDate
import kotlinx.coroutines.delay  // 시뮬레이션 지연
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.to

/**
 * 일기 관련 사항 API 통신으로 불러오는 역활
 */
@Singleton
class DiaryRepository @Inject constructor(private val apiService: ApiService) {

    // 초기 전체 일기 데이터 가져오기 (백엔드 호출 시뮬레이션)
    suspend fun fetchAllDiaries(): Map<String, DiaryEntry> {
        try {
            val fetched = fetchAllDiariesGet()

            return fetched.associate { entry ->
                entry.entry_date to DiaryEntry(
                    entry_date = entry.entry_date,
                    mood_code = entry.mood_code,
                    content = entry.content
                )
            }
        } catch (e: Exception) {
            throw kotlin.Exception("데이터 로드 실패: ${e.message}")
        }
    }
    suspend fun fetchAllDiariesGet(userId: Int = 2): List<DiaryResponse> =
        api.getDiaryList(userId)

    // 저장 (새 일기)
    suspend fun startDiaryTest(
        answers: DiaryEntry
        ): ResultDiary = api.submitDiaryCreate(answers)

    // 수정 (기존 일기 업데이트)
    suspend fun updateDiary(
        entry: DiaryEntry
    ): ResultDiary = api.submitDiaryCreate(entry)

    // 단일 일기 가져오기 (선택 시 사용)
    suspend fun getDiary(date: LocalDate): DiaryEntry? {
        delay(200)  // 시뮬레이션
        // TODO 실제: apiService.getDiary(date.toString())
        return null  // 임시: memoryStore에서 가져오지만, 여기선 ViewModel에서 처리
    }
    //일기 ai 호출
    suspend fun startDiaryAi(
        answers: DiaryAiEntry
    ): ResultAiDiary = api.aiDiaryCreate(answers)

}