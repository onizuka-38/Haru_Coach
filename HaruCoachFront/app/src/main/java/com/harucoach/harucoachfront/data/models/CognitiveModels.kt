package com.harucoach.harucoachfront.data.models

// ======= Start =======
data class Question(
    val questionNo: Int,
    val questionId: Int,
    val text: String,
    val category: String?
)

data class StartResponse(
    val sessionId: Long,
    val questions: List<Question>
)

// ======= Submit =======
data class AnswerItem(
    val questionNo: Int,
    val questionId: Int,
    val sttText: String? = null,
    val typedText: String? = null,
    val latencyMs: Int? = null
)

data class SubmitRequest(
    val sessionId: Long,
    val answers: List<AnswerItem>
)

// ======= Result =======
data class ResultResponse(
    val totalScore: Double,                              // 총점
    val categoryAverage: Map<String, Double>,            // 카테고리별 평균 점수
    val recentSessions: List<RecentSession>,       // 최근 3개 세션
    val summary: String,                               // 요약
    val grade: String                                  // 등급
)


data class RecentSession(
    val sessionId: Long,
    val finishedAt: String,      // ISO 8601 형식 날짜/시간 문자열
    val totalScore: Double       // Double 타입
)