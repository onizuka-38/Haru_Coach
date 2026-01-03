package com.harucoach.harucoachfront.ui.screens.cognitive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harucoach.harucoachfront.ui.theme.HaruGreen
import com.harucoach.harucoachfront.viewmodel.CognitiveViewModel

@Composable
fun CognitiveIntroScreen(
    onStart: () -> Unit = {},
    viewModel: CognitiveViewModel  // 파라미터로 받음 (HaruApp에서 전달)
) {
    // ViewModel 상태 관찰
    val uiState by viewModel.uiState.collectAsState()

    // 로딩 상태 확인 (questions를 불러오는 중)
    val isLoading = uiState.loading
    val hasQuestions = uiState.questions.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 큰 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFEFF5F1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "하루코치와 함께\n인지능력검사를\n시작해볼까요?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 36.sp
                    )
                    Text(
                        text = "안녕하세요! 저는 쿠모에요 😊\n" +
                                "지금부터 님의 생각과 기억을\n살짝 살펴보는 시간을 가져 볼게요.\n\n" +
                                "결과를 바탕으로, 님께 꼭 맞는 두뇌 활동을\n추천드릴게요. 시작해볼까요?",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }

        // 에러 메시지 표시
        if (uiState.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "⚠️ ${uiState.error}",
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFFC62828),
                    fontSize = 14.sp
                )
            }
        }

        // 하단 시작 버튼 (로딩 중에는 비활성화)
        Button(
            onClick = {
                // 1. 문제 로딩 시작
                viewModel.startTest(userId = 2, count = 10)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HaruGreen),
            enabled = !isLoading  // 로딩 중에는 비활성화
        ) {
            if (isLoading) {
                // 로딩 중
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Text("문제를 준비하고 있어요...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                // 대기 중
                Text("인지능력 검사하기", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(8.dp))
    }

    // 🔥 문제 로딩이 완료되면 자동으로 다음 화면으로 이동
    LaunchedEffect(hasQuestions) {
        if (hasQuestions && !isLoading) {
            onStart()  // TestScreen으로 이동
        }
    }
}