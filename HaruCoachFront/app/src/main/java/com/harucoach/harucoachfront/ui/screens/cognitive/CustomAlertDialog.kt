package com.harucoach.harucoachfront.ui.screens.cognitive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onContinueClick: () -> Unit,
    onStopClick: () -> Unit

) {
    Dialog(onDismissRequest = onDismissRequest) {
        val density = LocalDensity.current
        val fontSizeSp = with(density) { 20.dp.toSp() } // 👈 dp → sp 변환
        val fontSizeSp2 = with(density) { 25.dp.toSp() } // 👈 dp → sp 변환
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card for the text content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 상단 텍스트
                    Text(
                        text = "정말 검사를\n종료하시겠습니까?",
                        fontSize = fontSizeSp2,
                        color = Color.Black,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons are placed here, on the transparent dialog background
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // 왼쪽 '계속하기' 버튼
                Button(
                    onClick = onContinueClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // 초록색
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "계속하기",
                        color = Color.White,
                        fontSize = fontSizeSp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 오른쪽 '검사 그만하기' 버튼
                Button(
                    onClick = onStopClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // 빨간색
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "검사 그만하기",
                        color = Color.White,
                        fontSize = fontSizeSp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomAlertDialogPreview() {
    CustomAlertDialog(
        //다이얼로그 밖에 이벤트
        onDismissRequest = {},
        //계속하기 버튼 이벤트
        onContinueClick = {},
        //그만하기 버튼 이벤트
        onStopClick = {}
    )
}
