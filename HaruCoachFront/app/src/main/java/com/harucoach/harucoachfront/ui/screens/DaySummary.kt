package com.harucoach.harucoachfront.ui.screens


import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.harucoach.harucoachfront.R
import com.harucoach.harucoachfront.data.models.ResultAiDiary
import kotlinx.coroutines.delay

// 외부 URL 열기 함수
fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class) // TopAppBar를 사용하기 위해 필요합니다.
@Composable
fun DaySummary(
    navController: NavController,
    aiFeedbackResult: ResultAiDiary? // AI 분석 결과를 직접 매개변수로 받도록 변경
) {

    val context = LocalContext.current
    // BackHandler를 사용하여 뒤로가기 버튼 동작을 오버라이드합니다.
    BackHandler {
        navController.navigate(Routes.DIARY) {
            // DaySummary로 오기 전 스택에 DiaryScreen이 있다면 지우고 새로 띄웁니다.
            popUpTo(Routes.DIARY) { inclusive = true }
            launchSingleTop = true
        }
    }

    // DiaryViewModel을 사용하지 않으므로 aiResult 대신 aiFeedbackResult를 사용합니다.
    // val aiResult by diaryViewModel.aiResult.collectAsState() // 이 줄 제거

    // 각 UI 요소의 가시성을 제어할 상태 변수들
    val showRecommendationChat = remember { mutableStateOf(false) }
    val showEmotionReportCard = remember { mutableStateOf(false) }
    val showSummaryChatAndButtons = remember { mutableStateOf(false) }

    // LaunchedEffect를 사용하여 aiFeedbackResult가 변경될 때마다 1초 간격으로 UI 요소들을 나타나게 함
    LaunchedEffect(aiFeedbackResult) {
        // aiFeedbackResult가 변경될 때마다 애니메이션 상태 초기화
        showRecommendationChat.value = false
        showEmotionReportCard.value = false
        showSummaryChatAndButtons.value = false

        if (aiFeedbackResult != null) { // AI 결과 데이터가 있을 때만 애니메이션 시작
            showSummaryChatAndButtons.value = true
            delay(1000L) // 1초 대기

            showEmotionReportCard.value = true
            delay(1000L) // 1초 대기
            showRecommendationChat.value = true


        }
    }

    // ViewModel이 아닌 외부에서 데이터를 받으므로, 화면이 컴포지션에서 사라질 때 ViewModel의 AI 결과 초기화 로직은 필요 없습니다.
    // DisposableEffect(Unit) {
    //     onDispose {
    //         diaryViewModel.clearAiResult()
    //     }
    // }

    Column(
        modifier = Modifier
            .fillMaxSize()
            //.padding(paddingValues) // Scaffold의 패딩 적용
            .padding(horizontal = 5.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()), // 스크롤 가능하도록 변경
            verticalArrangement = Arrangement.Bottom // 스크롤뷰에서는 이 속성을 제거하거나 신중하게 사용
    ) {
        // aiFeedbackResult가 null이 아닐 때만 UI 콘텐츠 렌더링
        aiFeedbackResult?.let { data -> // 'data'는 이제 ResultAiDiary 객체입니다.
            // --- ResultAiDiary에서 필요한 정보 추출 ---
            val summaryText = data.summary

            val emotion = data.emotion_report.emotion ?: "알 수 없음"
            // empathy 필드를 콤마로 분리하여 키워드 리스트로 만듬
            //val keywords =  data.emotion_report.empathy?.split(",")?.map { it.trim() } ?: emptyList()
            val recommendationText =  data.emotion_report.life_tip ?: "특별한 추천이 없습니다."
            // AI가 추천한 유튜브 URL, 없을 경우 기본 유튜브 검색 URL
            val youtubeUrlToOpen = data.youtube.url ?: "https://www.youtube.com/results?search_query=건강 스트레칭"

            // 챗봇 요약 메시지 (ResultAiDiary의 summary 사용)
            if (showSummaryChatAndButtons.value) { // 버튼들과 함께 표시
                ChatBubble(
                    text = summaryText,
                    isUser = false, // 챗봇 메시지
                    modifier = Modifier.align(Alignment.Start) // 왼쪽 정렬
                )
                // 마지막 요소이므로 하단에 추가 Spacer는 필요 없을 수 있습니다.
                // 필요한 경우 추가: Spacer(modifier = Modifier.height(16.dp))
            }
            // 감정 리포트 카드 (day_summary 이미지 배경 적용)
            if (showEmotionReportCard.value) {
                EmotionReportCard(
                    emotion = emotion, // EmotionReport의 emotion 사용
                    //emoji = getEmojiForEmotion(emotion), // 감정에 맞는 이모지 생성 함수 사용
                    keywords = recommendationText, // EmotionReport의 empathy를 키워드로 사용
                    modifier = Modifier.align(Alignment.CenterHorizontally) // 중앙 정렬
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 챗봇 추천 메시지 (data.youtube.reason 사용)
            if (showRecommendationChat.value) {
                ChatBubble(
                    text = data.youtube.reason,
                    isUser = false, // 챗봇 메시지
                    modifier = Modifier.align(Alignment.Start) // 왼쪽 정렬
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // "네, 할게요" / "괜찮아요" 버튼들
            if (showRecommendationChat.value) { // 챗봇 요약 메시지와 함께 표시
                Column( // 버튼들을 세로로 쌓기 위해 Column 사용
                    modifier = Modifier.fillMaxWidth(), // Column이 전체 너비를 차지하도록 설정
                    horizontalAlignment = Alignment.End // Column의 내용을 오른쪽으로 정렬
                ) {
                    Spacer(modifier = Modifier.height(5.dp)) // 챗봇 메시지와 버튼 사이 16.dp 간격 추가
                    Button(
                        onClick = {
                            openUrl(context, youtubeUrlToOpen) // AI가 추천한 유튜브 URL 열기
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White), // 배경색을 흰색으로 변경
                        shape = RoundedCornerShape(24.dp), // 둥근 모서리
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)), // 테두리 추가 (1dp 두께, 초록색)
                        modifier = Modifier
                            .wrapContentHeight() // 내용에 따라 높이 조절
                            .padding(horizontal = 4.dp)
                    ) {
                        // 유튜브 추천 제목을 버튼 텍스트로 활용 (없으면 기본 텍스트)
                        Text("네, 할게요", color = Color(0xFF4CAF50), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp)) // 버튼들 사이에 세로 간격 추가

                    Button(
                        onClick = {
                            //navController.popBackStack() // 이전 화면으로 돌아가기
                            navController.navigate(Routes.DIARY) // 이전 화면으로 돌아가기
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White), // 배경색을 흰색으로 변경
                        shape = RoundedCornerShape(24.dp), // 둥근 모서리
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)), // 테두리 추가 (1dp 두께, 초록색)
                        modifier = Modifier
                            .wrapContentHeight() // 내용에 따라 높이 조절
                            .padding(horizontal = 4.dp)
                    ) {
                        Text("괜찮아요", color = Color(0xFF4CAF50), fontSize = 16.sp) // 텍스트 색상도 초록색으로 변경
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        } ?: run { // aiFeedbackResult가 null일 경우 (아직 데이터가 로드되지 않았거나 오류 발생)
            // 로딩 인디케이터나 에러 메시지 등을 표시할 수 있습니다.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("AI 분석 결과를 불러오는 중...", fontSize = 18.sp, color = Color.Gray)
                // CircularProgressIndicator() // 로딩 인디케이터를 여기에 추가할 수 있습니다.
            }
        }
    }
}

// 감정에 따라 적절한 이모지를 반환하는 헬퍼 함수
fun getEmojiForEmotion(emotion: String): String {
    return when (emotion) {
        "평온함" -> "😊"
        "기쁨" -> "😁"
        "슬픔" -> "😢"
        "분노" -> "😠"
        "불안" -> "😟"
        "피곤함" -> "😴"
        "긍정" -> "😄" // 추가 가능한 감정
        "부정" -> "😥"
        else -> "💬" // 기본 이모지
    }
}

// 채팅 메시지 버블 컴포저블
@Composable
fun ChatBubble(
    text: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) { // 챗봇 메시지일 경우 왼쪽에 구름 캐릭터 표시
            Image(
                painter = painterResource(id = R.drawable.normal_feelings), // normal_feelings 이미지 리소스 사용
                contentDescription = "Chatbot character",
                modifier = Modifier.size(40.dp) // 캐릭터 크기
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp, // 챗봇은 왼쪽 하단 뾰족
                        bottomEnd = if (isUser) 0.dp else 16.dp // 챗봇은 오른쪽 하단 둥글
                    )
                )
                .background(if (isUser) Color(0xFF4CAF50) else Color(0xFFC8E6C9)) // 사용자: 진한 초록, 챗봇: 연한 초록
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .weight(1f, fill = false) // 내용에 따라 너비 조절
        ) {
            Text(
                text = text,
                color = Color.Black, // 텍스트 색상
                fontSize = 16.sp
            )
        }
        if (isUser) { // 사용자 메시지일 경우 오른쪽에 구름 캐릭터 (필요하다면)
            Spacer(modifier = Modifier.width(8.dp))
            // Image(...) // 여기에 사용자 구름 캐릭터를 추가할 수 있습니다.
        }
    }
}

// 감정 리포트 카드 컴포저블
@Composable
fun EmotionReportCard(
    emotion: String,
    //emoji: String,
//    keywords: List<String>,
    keywords: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.9f) // 화면 너비의 90% 정도 사용
            .wrapContentHeight() // Box의 높이가 내용에 맞춰지도록 함
    ) {
        // day_summary 이미지를 배경으로 사용
        Image(
            painter = painterResource(id = R.drawable.day_summary), // day_summary.png 사용
            contentDescription = "Emotion Report Card Background",
            modifier = Modifier
                .fillMaxWidth() // Box의 너비에 맞게 이미지를 채움
                .wrapContentHeight() // 이미지의 종횡비를 유지하면서 높이 조절
                .align(Alignment.Center), // Box 중앙에 이미지 정렬
            contentScale = ContentScale.FillWidth // 너비에 맞춰 채우고 종횡비 유지
        )

        // 이미지 위에 텍스트 내용 배치
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // 이미지가 구름 모양이므로, 텍스트가 구름 안에 예쁘게 들어가도록 패딩 조정
                // 이 패딩 값은 실제 day_summary.png 이미지의 디자인에 따라 미세 조정이 필요할 수 있습니다.
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .align(Alignment.Center) // Box 중앙에 Column 정렬
        ) {
            // "감정 리포트" 제목
            Text(
                text = "감정 리포트",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Black // 배경이 연한 색이므로 텍스트 색상을 검정으로 확실히 지정
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 감정 정보
            Text(
                text = "감정: $emotion ", // 받아온 이모지 사용
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 키워드 정보
            Text(
                //text = "키워드: ${keywords.joinToString(" ")}", // 키워드를 공백으로 연결하여 표시
                text = "내용: $keywords", // 키워드를 공백으로 연결하여 표시
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}