package com.harucoach.harucoachfront.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harucoach.harucoachfront.data.models.LoginRequest
import com.harucoach.harucoachfront.data.models.LoginResponse
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import com.harucoach.harucoachfront.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel // 1. Hilt가 이 ViewModel을 관리하도록 지정
class LoginViewModel @Inject constructor( // 2. 생성자에 @Inject 추가
    private val repository: AuthRepository // 3. AuthRepository 주입
) : ViewModel() {
    private val _loginResult = mutableStateOf<LoginResponse?>(null)
    val loginResult: State<LoginResponse?> = _loginResult

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun performLogin(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = LoginRequest(username, password)
                // 4. Repository의 login 함수 호출 (API 호출 + 토큰 저장이 여기서 일어남)
                val result = repository.login(request)
                _loginResult.value = result
                _errorMessage.value = null

                // 토큰 저장 로직이 Repository로 이동했으므로 여기서 삭제
            } catch (e: Exception) {
                _errorMessage.value = "로그인 실패: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}