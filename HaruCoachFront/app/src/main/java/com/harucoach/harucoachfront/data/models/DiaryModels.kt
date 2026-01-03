package com.harucoach.harucoachfront.data.models

import java.time.LocalDate

// ======= get =======
data class DiaryResponse(
    val entry_id: Int,
    val user_id: Int,
    val entry_date: String,
    val mood_code: String,
    val content: String,
    val created_at: String
)

/*
*  DiaryListResponse : 일기목록 조회
*/
data class DiaryListResponse(
    val entries: List<DiaryResponse>
)


/**
 * 일기 목록 추가
 * TODO 백엔드 측과 확인 후 수정
 * entry_date: 일기 작성 날짜
 * mood_code: 기분 코드
 * content: 일기 내용
 */
// ======= Submit =======
data class DiaryEntry(
    val entry_date: String,
    val mood_code: String,
    val content: String
)


// ======= Result =======
data class ResultDiary(
    val entry_id: Int,
    val user_id: Int,
    val entry_date: String,
    val mood_code: String,
    val content: String
)

/**
 * 일기 ai
 * content: 일기 내용
 * entry_date: 일기 날짜
 * display_name: 쿠모
 */
// ======= AiSubmit =======
data class DiaryAiEntry(
    val content: String,
    val entry_date: String,
    val display_name: String
)
/**
 * 일기 ai 리턴값
 * summary: 일기 요약
 * emotion_report: 일기 내용
 * youtube: 유투브 내용
 */
// ======= ResultAi =======
data class ResultAiDiary(
    val summary: String,
    val emotion_report: EmotionReport,
    val youtube: Youtube,
)

data class EmotionReport(
    val emotion: String,
    val empathy: String,
    val life_tip: String
)
data class Youtube(
    val title: String,
    val url: String,
    val reason: String,
    val category: String
)