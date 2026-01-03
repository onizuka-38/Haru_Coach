package com.harucoach.harucoachfront.ui.screens.cognitive

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.harucoach.harucoachfront.R


@Composable
fun CustomFullAlertDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false) // 화면 꽉 채우기
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth(),  // 좌우 꽉 채움
            color = Color.Transparent

        ) {

            CustomDialogContent(onDismissRequest)
        }

    }
}

@Composable
fun CustomDialogContent(onDismissRequest: () -> Unit) {

    val density = LocalDensity.current
    val fontSizeSp2 = with(density) { 30.dp.toSp() } // 👈 dp → sp 변환
    val fontSizeSp = with(density) { 20.dp.toSp() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
                //.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent) // 배경색 투명하게 설정
            ) {


                Box( // Box를 추가하여 Text를 중앙 정렬
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // 상단 타이틀
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent) // 투명 배경
                    ) {
                        Text(
                            text = "도움창 닫기",
                            color = Color.Black,
                        )
                    }
                }
            }
            //버튼 아래 부분 오류가 ㅁ낳아서 이미지로 대체
            val custom_full_alert_dialog   = R.drawable.custom_full_alert_dialog
            Image(
                painter = painterResource(id = custom_full_alert_dialog),
                contentDescription = "프로필",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
            /*Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent) // 배경색 투명하게
                ,
                shape = RoundedCornerShape(8.dp)
            ) {
                // 1. Column을 추가하고 여기에 패딩을 적용합니다.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp), // 수평 패딩 제거
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent) // 배경색 투명하게 설정
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp) // 높이 조정 가능
                            .drawBehind {
                                val stroke = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(10f, 10f),
                                        0f
                                    )
                                )
                                drawRoundRect(
                                    color = Color.Black,
                                    size = size,
                                    cornerRadius = CornerRadius(8.dp.toPx()), // toPx()는 Dp의 확장 함수입니다.
                                    style = stroke
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                    }
                }
            }
            Text(
                text = "질문이표시됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = fontSizeSp

            )
            // 마이크 버튼
            Box(
                modifier = Modifier.size(160.dp)
                    .drawBehind {
                        val stroke = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f),
                                0f
                            )
                        )
                        drawRoundRect(
                            color = Color.Black,
                            size = size,
                            cornerRadius = CornerRadius(8.dp.toPx()),
                            style = stroke
                        )
                    },

                ) {
            }
            Text(
                text = "버튼을 클릭하면 녹음이 시작됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = fontSizeSp
            )
        }
        //하단 남은시간
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(0.dp,0.dp,0.dp,75.dp)
                .fillMaxWidth()
                .height(80.dp) // 높이 조정 가능
                .drawBehind {
                    val stroke = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f),
                            0f
                        )
                    )
                    drawRoundRect(
                        color = Color.Black,
                        size = size,
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = stroke
                    )
                },

            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "남은시간이 표시됩니다.",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = fontSizeSp2
            )
        }*/
    }
}
