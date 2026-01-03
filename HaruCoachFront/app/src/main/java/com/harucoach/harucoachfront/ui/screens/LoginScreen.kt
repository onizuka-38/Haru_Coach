package com.harucoach.harucoachfront.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.harucoach.harucoachfront.viewmodel.LoginViewModel

import androidx.hilt.navigation.compose.hiltViewModel // 1. hiltViewModel 임포트

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {  // NavController 추가
    // 2. viewModel() 대신 hiltViewModel() 사용
    // Hilt가 알아서 AuthRepository를 만들고,
    // 그것을 LoginViewModel에 주입하여 인스턴스를 생성해줌
    val viewModel: LoginViewModel = hiltViewModel()

    val loginResult by viewModel.loginResult
    val error by viewModel.errorMessage
    val isLoading by viewModel.isLoading

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = username, onValueChange = { username = it }, label = { Text("사용자명") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("비밀번호") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.performLogin(username, password) },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "로그인 중..." else "로그인")
        }
        if (error != null) {
            Text("오류: $error", color = MaterialTheme.colorScheme.error)
        }
        loginResult?.let {
            // 성공 시 네비게이션
            LaunchedEffect(Unit) { navController.navigate("home") }  // 자동 이동
            Text("성공: 토큰 ${it.access_token}")
        }
    }
}