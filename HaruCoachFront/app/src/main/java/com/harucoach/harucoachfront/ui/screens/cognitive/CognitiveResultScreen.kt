package com.harucoach.harucoachfront.ui.screens.cognitive

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.harucoach.harucoachfront.data.models.RecentSession
import com.harucoach.harucoachfront.ui.screens.Routes
import com.harucoach.harucoachfront.viewmodel.CognitiveViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CognitiveResultScreen(
    navController: NavHostController,
    viewModel: CognitiveViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val result = uiState.result
    BackHandler(enabled = true) {}//뒤로가기 막기

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 결과가 없으면 에러 표시
        if (result == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "결과를 불러올 수 없습니다.",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
            return@Column
        }
        // 1. 닉네임 + 등급 카드 (그라데이션)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4CAF50), // 초록
                                Color(0xFF5C6BC0) // 파랑
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Text(
                    text = "{닉네임}님은\n정상 등급입니다.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 32.sp
                )
            }
        }
        // 2. 최근 인지 능력 테스트 결과 (라인 차트)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "최근 인지 능력 테스트 결과",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "지난 3개의 인지 능력 점수 변화",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(20.dp))
                // 라인 차트
                LineChart(
                    sessions = result.recentSessions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
        // 3. 전반적인 인지 능력 (스파이더 차트)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "전반적인 인지 능력",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "영역별 인지 능력 요약",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(20.dp))
                // 스파이더 차트
                SpiderChart(
                    categories = result.categoryAverage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }
        // 4. 한줄 요약 (추후 논의)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "💡",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "한줄요약(추후 논의)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111111)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = result.summary,
                    fontSize = 15.sp,
                    color = Color(0xFF333333),
                    lineHeight = 24.sp
                )
            }
        }
        // 5. 홈으로 돌아가기 버튼
        Button(
            onClick = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = false }
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
                text = "홈으로 돌아가기",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ==================== 라인 차트 ====================
@Composable
private fun LineChart(
    sessions: List<RecentSession>,
    modifier: Modifier = Modifier
) {
    if (sessions.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("데이터가 없습니다.", color = Color.Gray)
        }
        return
    }
    // 최근 3개만 표시 (역순)
    val displaySessions = sessions.take(3).reversed()
    Canvas(modifier = modifier.padding(16.dp)) {
        val width = size.width
        val height = size.height
        val padding = 40f
        // Y축 범위 (0-100)
        val maxY = 100f
        val minY = 0f
        // X축 간격
        val xStep = (width - padding * 2) / (displaySessions.size - 1).coerceAtLeast(1)
        // 배경 그리드 그리기
        for (i in 0..4) {
            val y = padding + (height - padding * 2) * i / 4
            drawLine(
                color = Color(0xFFEEEEEE),
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1f
            )
        }
        // 라인 그리기
        val path = Path()
        displaySessions.forEachIndexed { index, session ->
            val x = padding + xStep * index
            val scoreRatio = (session.totalScore / maxY).toFloat()
            val y = padding + (height - padding * 2) * (1 - scoreRatio)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            // 점 그리기
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = 8f,
                center = Offset(x, y)
            )
        }
        // 라인 그리기
        drawPath(
            path = path,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 4f)
        )
        // X축 레이블 (날짜)
        displaySessions.forEachIndexed { index, session ->
            val x = padding + xStep * index
            // 날짜 표시는 drawContext.canvas.nativeCanvas를 사용해야 하므로 생략
            // 실제로는 Text Composable을 별도로 배치해야 합니다
        }
    }
    // X축 레이블 표시
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        displaySessions.forEach { session ->
            Text(
                text = formatDateShort(session.finishedAt),
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

// ==================== 스파이더 차트 ====================
@Composable
private fun SpiderChart(
    categories: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("데이터가 없습니다.", color = Color.Gray)
        }
        return
    }
    val categoryList = categories.toList()
    val numCategories = categoryList.size
    // 🔥 BoxWithConstraints로 크기 미리 계산
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer() // New: For dynamic sizing
        val boxWidth = constraints.maxWidth.toFloat()
        val boxHeight = constraints.maxHeight.toFloat()
        val centerX = boxWidth / 2
        val centerY = boxHeight / 2
        val radius = min(boxWidth, boxHeight) / 2 - 60f
        val labelRadius = min(boxWidth, boxHeight) / 2 - 20f
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 배경 웹 그리기 (5개 레벨: 0, 25, 50, 75, 100)
                for (level in 1..5) {
                    val levelRadius = radius * level / 5
                    val path = Path()
                    for (i in 0 until numCategories) {
                        val angle = (PI / 2 - 2 * PI * i / numCategories).toFloat()
                        val x = centerX + levelRadius * cos(angle)
                        val y = centerY - levelRadius * sin(angle)
                        if (i == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }
                    path.close()
                    drawPath(
                        path = path,
                        color = Color(0xFFEEEEEE),
                        style = Stroke(width = 1f)
                    )
                }
                // 축 그리기
                for (i in 0 until numCategories) {
                    val angle = (PI / 2 - 2 * PI * i / numCategories).toFloat()
                    val x = centerX + radius * cos(angle)
                    val y = centerY - radius * sin(angle)
                    drawLine(
                        color = Color(0xFFDDDDDD),
                        start = Offset(centerX, centerY),
                        end = Offset(x, y),
                        strokeWidth = 1f
                    )
                }
                // 데이터 영역 그리기
                val dataPath = Path()
                categoryList.forEachIndexed { i, (_, score) ->
                    val angle = (PI / 2 - 2 * PI * i / numCategories).toFloat()
                    val scoreRadius = radius * (score / 100.0).toFloat()
                    val x = centerX + scoreRadius * cos(angle)
                    val y = centerY - scoreRadius * sin(angle)
                    if (i == 0) {
                        dataPath.moveTo(x, y)
                    } else {
                        dataPath.lineTo(x, y)
                    }
                }
                dataPath.close()
                // 채워진 영역
                drawPath(
                    path = dataPath,
                    color = Color(0x805C6BC0) // 반투명 파랑
                )
                // 테두리
                drawPath(
                    path = dataPath,
                    color = Color(0xFF5C6BC0),
                    style = Stroke(width = 3f)
                )
                // 점 그리기
                categoryList.forEachIndexed { i, (_, score) ->
                    val angle = (PI / 2 - 2 * PI * i / numCategories).toFloat()
                    val scoreRadius = radius * (score / 100.0).toFloat()
                    val x = centerX + scoreRadius * cos(angle)
                    val y = centerY - scoreRadius * sin(angle)
                    drawCircle(
                        color = Color(0xFF5C6BC0),
                        radius = 6f,
                        center = Offset(x, y)
                    )
                }
            }
            // 카테고리 레이블 (BoxWithConstraints 내부에서 계산)
            categoryList.forEachIndexed { i, (category, _) ->
                val angle = (PI / 2 - 2 * PI * i / numCategories).toFloat()
                val labelX = centerX + labelRadius * cos(angle)
                val labelY = centerY - labelRadius * sin(angle)
                val cosA = cos(angle)
                val sinA = sin(angle)
                val textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                val measured = textMeasurer.measure(category, textStyle)
                val width = measured.size.width.toFloat()
                val height = measured.size.height.toFloat()
                // New: Dynamic dx/dy for alignment
                val dx = if (kotlin.math.abs(cosA) > kotlin.math.abs(sinA)) {
                    if (cosA > 0) 0f else -width // Right: start at point, Left: end at point
                } else -width / 2
                val dy = if (kotlin.math.abs(sinA) > kotlin.math.abs(cosA)) {
                    if (sinA > 0) -height else 0f // Top: bottom at point, Bottom: top at point
                } else -height / 2
                Text(
                    text = category,
                    style = textStyle,
                    modifier = Modifier
                        .offset(
                            x = with(density) { (labelX + dx).toDp() },
                            y = with(density) { (labelY + dy).toDp() }
                        )
                )
            }
        }
    }
}

// ==================== 유틸리티 함수 ====================
private fun formatDateShort(isoString: String): String {
    return try {
        // "2025-11-16T11:08:39" → "2025.11"
        val parts = isoString.split("T")[0].split("-")
        "${parts[0]}.${parts[1]}"
    } catch (e: Exception) {
        isoString
    }
}