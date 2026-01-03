package com.harucoach.harucoachfront.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import com.harucoach.harucoachfront.R

/**
 * BottomSheet 내부에 표시될 AI 피드백 화면의 콘텐츠입니다.
 */

@Composable
fun InfiniteAnimation(
    navController: NavController, // NavController 인자 추가
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest() // 기존 다이얼로그 닫기 로직 실행
            navController.navigate("day_summary_route") { // DaySummary 화면으로 이동
                popUpTo("ai_feedback_route") { inclusive = true } // 현재 AI 피드백 화면을 백 스택에서 제거
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // 화면 꽉 채우기
            // `AiFeedback` 내부에 BackHandler가 있으므로 이곳의 `dismissOnBackPress`는 `true`로 두어 `onDismissRequest`가 트리거되도록 합니다.
            // 하지만 사용자 요청으로 뒤로 가기를 막고 싶다면, `AiFeedback` 컴포저블 내부에서 `onDismissRequest`를 직접 호출하는 로직이 필요합니다.
            // 여기서는 `Dialog` 자체의 뒤로 가기 및 외부 클릭 방지 속성을 유지하고, onDismissRequest는 `AiFeedback` 내부의 특정 액션(예: 타이머 종료 후 자동 닫힘)에 의해 호출된다고 가정합니다.
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),  // 좌우 꽉 채움
            color = Color.Transparent
        ) {
            // AiFeedback 컴포저블은 onDismissRequest만 받도록 유지
            // 만약 AiFeedback 내부에서 ViewModel이 필요하다면 AiFeedback 자체에 인자를 추가하세요.
            AiFeedback(onDismissRequest = onDismissRequest)
        }
    }
}

@Composable
fun AiFeedback(
    onDismissRequest: () -> Unit,
) {
    // 뒤로가기 버튼 비활성화 (DialogProperties에서 이미 설정되었지만, 명시적으로 유지)
    BackHandler(enabled = true) {
        // 뒤로가기 버튼이 눌려도 아무 작업도 하지 않습니다.
    }

    val infiniteTransition = rememberInfiniteTransition(label = "HeartSizeAnimation")

    val heartSize by infiniteTransition.animateFloat(
        initialValue = 250.0f,
        targetValue = 300.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                delayMillis = 100,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "쿠무tSizeValue"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(heartSize.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.speech_bubble),
                        contentDescription = "분석 중 말풍선",
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "쿠무가 {닉네임}의\n하루를 요약중이에요\n",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .components {
                    add(ImageDecoderDecoder.Factory())
                }
                .build()

            AsyncImage(
                model = R.drawable.kumu,
                contentDescription = "쿠무 캐릭터 애니메이션",
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.End)
                    .padding(end = 16.dp)
            )
        }
    }
}