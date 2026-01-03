package com.harucoach.harucoachfront.ui.screens.cognitive

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.harucoach.harucoachfront.ui.screens.Routes
import com.harucoach.harucoachfront.viewmodel.CognitiveViewModel

@Composable
fun CognitiveWaitingScreen(
    navController: NavHostController,  // ← navController 파라미터 추가!
    viewModel: CognitiveViewModel
) {
    BackHandler(enabled = true) {

    }

    // ViewModel의 uiState를 관찰
    val uiState by viewModel.uiState.collectAsState()

    // 결과가 있는지 확인
    val hasResult = uiState.result != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) 상단 메시지
        Text(
            text = "수고하셨어요,",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
            textAlign = TextAlign.Center,
            color = Color(0xFF111111)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "{닉네임}님 🌿",  // TODO: 실제 닉네임으로 변경
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            color = Color(0xFF0F7A49)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "쿠모가 결과를 정리하고 있어요.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            textAlign = TextAlign.Center,
            color = Color(0xFF6B6B6B)
        )
        Spacer(modifier = Modifier.height(30.dp))

        // 2) 로딩 중이면 스피너, 결과 있으면 완료 메시지
        if (!hasResult) {
            // 로딩 스피너 표시
            LoadingSpinnerWithLabel()
        } else {
            // 결과 완료 표시
            Text(
                text = "✅ 결과 준비 완료!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF0F7A49)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3) 하단 버튼 또는 상태 카드
        if (!hasResult) {
            // 로딩 중일 때 - 상태 카드
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "검사결과를 생성하고 있습니다...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // 결과가 있을 때 - 결과 보기 버튼
            Button(
                onClick = {
                    // ResultScreen으로 이동
                    navController.navigate(Routes.COGNITIVE_RESULT) {
                        popUpTo(Routes.COGNITIVE_WAITING) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C853)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "결과 확인하기",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LoadingSpinnerWithLabel() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .rotate(rotation)
        ) {
            CircularProgressIndicator(
                strokeWidth = 6.dp,
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF616161)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Loading",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B6B6B))
        )
    }
}