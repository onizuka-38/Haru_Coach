package com.harucoach.harucoachfront.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harucoach.harucoachfront.R

/** ---- 로그인 화면 ---- */
@Composable
fun LoginScreen2(
    onLoginClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    val isFormValid = id.isNotBlank() && pw.isNotBlank()
    Scaffold(
       bottomBar = {
           // 하단 문구
           Row(
               horizontalArrangement = Arrangement.Center,
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(bottom = 20.dp)
                   .navigationBarsPadding()
           ) {
               Text("계정이 없으신가요?", style = MaterialTheme.typography.bodyMedium, fontSize = 24.sp)
               Spacer(Modifier.width(8.dp))
               TextButton(onClick = onJoinClick) {
                   Text("함께해요", color = MaterialTheme.colorScheme.primary, fontSize = 24.sp)
               }
           }
       }
    ) { padding ->
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

            //Spacer(Modifier.height(50.dp))

            // 입력 폼
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {


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


        }
    }
}
