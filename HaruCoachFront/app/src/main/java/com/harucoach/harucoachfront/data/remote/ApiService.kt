package com.harucoach.harucoachfront.data.remote



import com.harucoach.harucoachfront.data.models.*
import retrofit2.http.*

interface ApiService  {
    //폼 방식
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    // 세션 생성 + 문항 조회
    @GET("cognitive/start")
    suspend fun getCognitiveQuestions(
        @Query("user_id") userId: Int = 1, // 로그인 연동 전 임시
        @Query("count") count: Int = 10,
        @Query("category") category: String? = null
    ): StartResponse

    // 답안 제출 + 결과
    @POST("cognitive/submit")
    suspend fun submitCognitiveAnswers(
        @Body body: SubmitRequest
    ): ResultResponse


    // 특정 사용자의 모든 일기 목록을 조회합니다.
    @GET("diary/list")
    suspend fun getDiaryList(
        @Query("user_id") userId: Int = 2, // 로그인 연동 전 임시
    ): List<DiaryResponse>

    // 일기 저장
    @POST("diary/create/?user_id=2")
    suspend fun submitDiaryCreate(
        @Body body: DiaryEntry
    ): ResultDiary

    // 일기 AI
    @POST("diary/ai")
    suspend fun aiDiaryCreate(
        @Body body: DiaryAiEntry
    ): ResultAiDiary


}