package com.harucoach.harucoachfront.data.repository

import com.harucoach.harucoachfront.data.models.*
import com.harucoach.harucoachfront.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CognitiveRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun startTest(
        userId: Int = 1,
        count: Int = 10,
        category: String? = null
    ): StartResponse = api.getCognitiveQuestions(userId, count, category)

    suspend fun submitAnswers(
        sessionId: Long,
        answers: List<AnswerItem>
    ): ResultResponse = api.submitCognitiveAnswers(SubmitRequest(sessionId, answers))
}
