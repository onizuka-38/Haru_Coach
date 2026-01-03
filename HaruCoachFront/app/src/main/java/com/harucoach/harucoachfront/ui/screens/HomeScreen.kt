package com.harucoach.harucoachfront.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import com.harucoach.harucoachfront.R
import com.harucoach.harucoachfront.ui.theme.HaruGreen
import com.harucoach.harucoachfront.ui.theme.HaruGreenLight
import kotlinx.coroutines.launch

// UI에서 사용하기 위한 UI상태변수 선언
var stepCountData by mutableStateOf<List<Int>>(emptyList())
// 권한 부여 여부를 추적하는 상태 변수
var healthConnectPermissionsGranted by mutableStateOf(false)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Composable의 생명주기를 따르는 코루틴 스코프

    // HealthConnectManager 인스턴스 생성
    val healthConnectManager = remember { HealthConnectManager(context) }

    // 데이터 패칭 함수 (LaunchedEffect 안에서 호출될 수 있도록 Composable 내부로 옮김)
    fun fetchAndSend(onDataFetched: (List<Int>) -> Unit) {
        scope.launch { // rememberCoroutineScope에서 얻은 스코프 사용
            try {
                val stepRecords = healthConnectManager.readStepCounts()
                // 오늘 걸음수 총합을 계산하여 UI에 표시할 수 있도록 Int로 변경 (혹은 List<StepsRecord> 그대로 사용)
                val totalStepsToday = stepRecords.sumOf { it.count.toInt() }
                Log.d("HEALTH_SYNC", "오늘의 총 걸음수: $totalStepsToday")
                onDataFetched(listOf(totalStepsToday)) // 현재는 총합만 List<Int>로 전달
            } catch (e: Exception) {
                Log.e("HEALTH_SYNC", "에러 발생", e)
            }
        }
    }

    // Health Connect 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
        onResult = { granted: Set<String> ->
            Log.d("HEALTH_SYNC", "권한 요청 결과: $granted")
            if (granted.containsAll(healthConnectManager.permissions)) {
                healthConnectPermissionsGranted = true // 권한 부여 상태 업데이트
                // 권한이 부여되면 데이터를 가져오도록 LaunchedEffect를 트리거
            } else {
                Log.e("HEALTH_SYNC", "권한 요청 실패: ${healthConnectManager.permissions.minus(granted)}")
                healthConnectPermissionsGranted = false
            }
        }
    )

    // LaunchedEffect를 사용하여 컴포넌트 생명주기에 맞춰 권한 확인 및 요청
    LaunchedEffect(Unit) {
        // 앱 시작 시 Health Connect 권한이 이미 부여되었는지 확인
        val grantedPermissions = healthConnectManager.healthConnectClient.permissionController.getGrantedPermissions()
        val hasAllPermissions = grantedPermissions.containsAll(healthConnectManager.permissions)

        if (!hasAllPermissions) {
            // 권한이 없으면 요청
            permissionLauncher.launch(healthConnectManager.permissions)
        } else {
            // 이미 권한이 있으면 바로 데이터 패칭을 위해 상태 업데이트
            healthConnectPermissionsGranted = true
        }
    }

    // healthConnectPermissionsGranted 상태가 true일 때만 데이터 패칭 실행
    LaunchedEffect(healthConnectPermissionsGranted) {
        if (healthConnectPermissionsGranted) {
            fetchAndSend { stepData ->
                stepCountData = stepData
                // UI에 데이터를 업데이트하는 작업을 여기에 추가하면 됩니다.
            }
        }
    }


    BackHandler(enabled = true) { /* 뒤로가기 버튼을 눌러도 아무 동작도 하지 않습니다. */ }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 상단 인사 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "안녕하세요, {닉네임}님!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "매일 꾸준히 기록하며 건강을 관리하고 계시네요!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(HaruGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👣", fontSize = 14.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("오늘의 걸음수", fontSize = 13.sp, color = Color.Gray)
                            // stepCountData는 List<Int> 이므로, 첫 번째 요소를 가져오거나 합계를 표시할 수 있습니다.
                            // 여기서는 첫 번째 요소를 표시하도록 가정합니다.
                            Text(
                                text = if (stepCountData.isNotEmpty()) "${stepCountData[0]} 걸음" else "데이터 없음",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(HaruGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.normal_feelings),
                        contentDescription = "평범함",
                        modifier = Modifier.size(48.dp) // 이미지 크기 조절
                    )
                }
            }
        }

        // 🔹 여기서 아래 두 함수(LargeFeatureCard, SmallFeatureCard)를 호출함
        LargeFeatureCard(
            title = "인지 능력 검사",
            icon = Icons.Default.Psychology,
            onClick = { onNavigate(Routes.COGNITIVE) }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SmallFeatureCard(
                title = "오늘의 일기",
                icon = Icons.Default.Edit,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(Routes.DIARY) }
            )
            SmallFeatureCard(
                title = "오늘의 학습",
                icon = Icons.Default.Book,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(Routes.NUMBERS_GAME) }
            )
        }
    }
}

@Composable
private fun LargeFeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = HaruGreen,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(title, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SmallFeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = HaruGreen,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}