package com.harucoach.harucoachfront.data.repository

import com.harucoach.harucoachfront.data.PreferencesManager
import com.harucoach.harucoachfront.data.models.LoginRequest
import com.harucoach.harucoachfront.data.models.LoginResponse
import com.harucoach.harucoachfront.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 이 Repository는 앱 전체에서 하나의 인스턴스만 사용
class  AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefsManager: PreferencesManager
) {
    /**
     * 로그인을 수행하고 성공 시 토큰을 저장
     */
    suspend fun login(request: LoginRequest): LoginResponse {
        // 1. API를 통해 로그인 시도
        val response = apiService.login(request.username, request.password)

        // 2. 성공 시 토큰을 PreferencesManager에 저장
        if (response.access_token.isNotEmpty()) {
            prefsManager.saveAuthToken(response.access_token)
        }

        return response
    }

    /**
     *  나중에 로그아웃 기능도 여기에 추가할 수 있습니다.
     */
    suspend fun logout() {
        prefsManager.clearAuthToken()
        // 필요시 서버에 /logout API 호출
    }
}