package com.harucoach.harucoachfront.ui.screens

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Locale
import kotlin.random.Random

// 게임의 현재 상태를 관리하기 위한 데이터 클래스
data class GameState(
    val number1: Int = (10..99).random(), // 2자리 숫자 (10~99)
    val number2: Int = (1..9).random(),   // 1자리 숫자 (1~9)
) {
    // 실제 정답 계산
    val answer: Int
        get() = number1 + number2
}

@Composable
fun NumbersGameScreen(navController: NavController) {
    // --- 상태 관리 ---
    // 게임 상태 (문제 숫자)
    var gameState by remember { mutableStateOf(GameState()) }
    // 사용자가 입력한 답
    var userAnswer by remember { mutableStateOf("") }
    // 결과 다이얼로그를 보여줄지 여부
    var showResultDialog by remember { mutableStateOf(false) }
    // 다이얼로그에 표시될 메시지
    var dialogMessage by remember { mutableStateOf("") }

    // 현재 Compose 컨텍스트에서 Context 객체를 가져옴
    val context = LocalContext.current    // 뒤로가기 버튼 비활성화
    // Compose 상태 변수들 정의
    // `remember`와 `mutableStateOf`를 사용하여 상태가 변경될 때 UI가 자동으로 업데이트되도록 함

    var errorMessage by remember { mutableStateOf("") } // 오류 메시지를 저장
    var isListening by remember { mutableStateOf(false) } // 음성 인식기 작동 여부

    // SpeechRecognizer 인스턴스 생성 및 기억
    // 컴포저블이 리컴포즈되어도 동일한 인스턴스를 유지
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    // 음성 인식 인텐트 설정 및 기억
    // 음성 인식 서비스에 전달할 추가 정보들을 정의
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            // 음성 인식 서비스를 호출하는 패키지 이름을 지정
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            // 자유 형식 음성 인식을 위한 언어 모델 설정
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // 기기의 기본 언어로 음성 인식 설정 (한국어 등)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())

        }
    }

    // DisposableEffect를 사용하여 SpeechRecognizer의 생명주기를 관리
    // 컴포저블이 처음 구성될 때 리스너를 설정하고, 제거될 때 리소스를 해제함
    DisposableEffect(Unit) {
        // RecognitionListener 구현: 음성 인식 이벤트에 대한 콜백 처리
        val listener = object : RecognitionListener {
            // 음성 인식이 시작될 준비가 되었을 때 호출됨
            override fun onReadyForSpeech(params: Bundle?) {
                errorMessage = "" // 오류 메시지 초기화
                isListening = true // 녹음 중 상태로 변경
                Toast.makeText(context, "녹음 시작...", Toast.LENGTH_SHORT).show() // 녹음 시작 토스트 메시지
            }

            // 사용자가 말하기 시작했을 때 호출됨
            override fun onBeginningOfSpeech() {
                // 이 콜백에서 추가적인 동작을 수행할 수 있음
            }

            // 입력 볼륨(RMS)이 변경되었을 때 호출됨
            override fun onRmsChanged(rmsdB: Float) {
                // 입력 볼륨 변화에 따른 UI 피드백 등을 구현할 수 있음
            }

            // 음성 데이터 버퍼가 수신되었을 때 호출됨
            override fun onBufferReceived(buffer: ByteArray?) {
                // 수신된 음성 데이터 버퍼에 대한 처리를 할 수 있음
            }

            // 사용자가 말하기를 멈췄을 때 호출됨
            override fun onEndOfSpeech() {
                //isListening = false // 녹음 중 상태 해제
                //Toast.makeText(context, "녹음 종료", Toast.LENGTH_SHORT).show() // 녹음 종료 토스트 메시지
            }

            // 음성 인식 중 오류가 발생했을 때 호출됨
            override fun onError(error: Int) {
                isListening = false // 오류 발생 시 녹음 중 상태 해제
                // 발생한 오류 코드에 따라 적절한 오류 메시지 생성
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "오디오 오류"
                    SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 부족"
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 시간 초과"
                    SpeechRecognizer.ERROR_NO_MATCH -> "일치하는 결과 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기 사용 중"
                    SpeechRecognizer.ERROR_SERVER -> "서버 오류"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 시간 초과"
                    else -> "알 수 없는 오류: $error"
                }
                errorMessage = "오류: $errorMsg" // 오류 메시지 업데이트
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show() // 오류 메시지 토스트
            }

            // 최종 음성 인식 결과가 나왔을 때 호출됨
            override fun onResults(results: Bundle?) {
                // 인식된 텍스트 목록을 가져옴
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    userAnswer = matches[0] // 첫 번째 인식 결과를 recordedText에 저장

                }
            }

            // 부분적인 음성 인식 결과가 나왔을 때 호출됨 (실시간 업데이트에 사용)
            override fun onPartialResults(partialResults: Bundle?) {
                // 인식된 부분 텍스트 목록을 가져옴
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                /* if (!matches.isNullOrEmpty()) {
                     recordedText = matches[0] // 첫 번째 부분 인식 결과를 recordedText에 표시
                 }*/
            }

            // 기타 이벤트가 발생했을 때 호출됨
            override fun onEvent(eventType: Int, params: Bundle?) {
                // 추가적인 이벤트를 처리할 수 있음
            }
        }

        // SpeechRecognizer에 리스너 설정
        speechRecognizer.setRecognitionListener(listener)

        // 컴포저블이 화면에서 제거될 때 호출되는 클린업 람다
        onDispose {
            speechRecognizer.destroy() // SpeechRecognizer 리소스 해제
        }
    }


    // --- 함수 정의 ---
    // 새로운 문제를 생성하는 함수
    fun generateNewProblem() {
        gameState = GameState()
        userAnswer = "" // 입력 필드 초기화
    }

    // 정답을 확인하는 함수
    fun checkAnswer() {
        val isCorrect = userAnswer.toIntOrNull() == gameState.answer
        dialogMessage = if (isCorrect) "정답입니다!" else "틀렸습니다. 다시 풀어보세요."
        showResultDialog = true
    }

    // --- UI 레이아웃 ---
    // Box를 사용하여 전체 화면을 채우고 중앙 정렬
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp) // 요소들 사이의 수직 간격
        ) {
            // 2. 문제가 나오는 칸
            Text(
                text = "${gameState.number1} + ${gameState.number2} = ?",
                fontSize = 48.sp, // 큰 글씨 크기
                style = MaterialTheme.typography.headlineLarge
            )

            // 3. 답을 적는 칸 + 마이크 버튼
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = { Text("정답을 입력하세요") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // 숫자 키보드만 표시
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {

                        speechRecognizer.startListening(speechRecognizerIntent)
                    }) {
                        Icon(Icons.Default.Mic, contentDescription = "음성으로 답하기")
                    }
                }
            )

            // 4. 정답 확인 버튼
            Button(
                onClick = { checkAnswer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("정답 확인", fontSize = 18.sp)
            }
        }
    }

    // 6. 결과 다이얼로그
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("결과") },
            text = { Text(dialogMessage) },
            confirmButton = {
                // 7. "다음 문제 풀기" 버튼
                Button(
                    onClick = {
                        showResultDialog = false
                        generateNewProblem()
                    }
                ) {
                    Text("다음 문제 풀기")
                }
            },
            dismissButton = {
                // 8. "그만하기" 버튼
                Button(
                    onClick = {
                        showResultDialog = false
                        navController.navigate("home") { // 홈 화면으로 이동
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray) // 색상 변경
                ) {
                    Text("그만하기")
                }
            }
        )
    }
}

// 개발 중 UI를 확인하기 위한 Preview 함수
@Preview(showBackground = true)
@Composable
fun NumbersGamePreview() {
    // rememberNavController()는 실제 네비게이션 동작을 테스트하기 위해 필요합니다.
    NumbersGameScreen(navController = rememberNavController())
}