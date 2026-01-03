package com.harucoach.harucoachfront

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.text.isNotBlank

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(onLoginClick = { /* TODO */ }, onJoinClick = { /* TODO */ })
                }
            }
        }
    }
}

/** ---- 나눔스퀘어 폰트 지정 ---- */
val NanumSquareNeo = FontFamily(
    Font(R.font.nanum_square_neo_variable, FontWeight.Normal)
)

/** ---- 나눔스퀘어 Typography ---- */
val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = NanumSquareNeo,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NanumSquareNeo,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NanumSquareNeo,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = NanumSquareNeo,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

/** ---- 테마 ---- */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Color(0xFF3AA85B), // 버튼 초록
        onPrimary = Color.White,
        surface = Color.White,
        onSurface = Color(0xFF111111)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,  // 👈 나눔스퀘어 적용!
        content = content
    )
}

/** ---- 로그인 화면 ---- */
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    val isFormValid = id.isNotBlank() && pw.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 로고
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Haru Coach Logo",
                modifier = Modifier
                    .width(162.dp)
                    .height(152.dp)
            )
        }

        Spacer(Modifier.height(50.dp))

        // 입력 폼
        Column(Modifier.fillMaxWidth()) {

            // 아이디 라벨
            Text(
                text = "아이디",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB8C0CC),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = id,
                onValueChange = { id = it },
                singleLine = true,
                placeholder = { Text("아이디", color = Color(0xFFB8C0CC), fontSize = 24.sp) },
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.mail),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF2F4F6),
                    unfocusedContainerColor = Color(0xFFF2F4F6),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(44.dp))

            // 비밀번호
            Text(
                text = "비밀번호",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB8C0CC),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = pw,
                onValueChange = { pw = it },
                singleLine = true,
                placeholder = { Text("비밀번호", color = Color(0xFFB8C0CC), fontSize = 24.sp) },
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Image(
                            painter = painterResource(R.drawable.hide),
                            contentDescription = if (pwVisible) "비밀번호 숨기기" else "비밀번호 표시",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                visualTransformation = if (pwVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF2F4F6),
                    unfocusedContainerColor = Color(0xFFF2F4F6),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { if (isFormValid) onLoginClick() },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            ) {
                Text("들어가기", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(200.dp))

        // 하단 문구
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("계정이 없으신가요?", style = MaterialTheme.typography.bodyMedium, fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onJoinClick) {
                Text("함께해요", color = MaterialTheme.colorScheme.primary, fontSize = 24.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLogin() {
    AppTheme { LoginScreen(onLoginClick = {}, onJoinClick = {}) }
}
