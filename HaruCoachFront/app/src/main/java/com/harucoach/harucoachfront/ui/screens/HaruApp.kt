package com.harucoach.harucoachfront.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // navArgument 임포트 추가
import androidx.navigation.NavType // NavType 임포트 추가
import com.harucoach.harucoachfront.ui.screens.cognitive.CognitiveIntroScreen
import com.harucoach.harucoachfront.ui.screens.cognitive.CognitiveTestScreen
import com.harucoach.harucoachfront.ui.screens.cognitive.CognitiveWaitingScreen
import com.harucoach.harucoachfront.ui.screens.cognitive.CognitiveResultScreen
import com.harucoach.harucoachfront.viewmodel.CognitiveViewModel
import com.harucoach.harucoachfront.data.models.ResultAiDiary // ResultAiDiary 임포트
import com.google.gson.Gson // Gson 임포트
import java.net.URLDecoder // URLDecoder 임포트
import java.nio.charset.StandardCharsets // StandardCharsets 임포트

object Routes {
    const val HOME = "home"
    const val COGNITIVE = "cognitive"
    const val COGNITIVE_TEST = "cognitive_test"
    const val COGNITIVE_WAITING = "cognitive_waiting"
    const val COGNITIVE_RESULT = "cognitive_result"
    const val DIARY = "diary"
    const val LEARN = "learn"
    const val MY = "my"
    const val DAY_SUMMARY = "day_summary"
    const val DAY_SUMMARY_WITH_ARG = "day_summary/{aiFeedbackResultJson}" // 인자를 받는 경로 정의
    const val NUMBERS_GAME = "numbers_game"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaruApp() {
    val nav = rememberNavController()
    val currentRoute = nav.currentRoute()

    // Activity 범위의 ViewModel 생성 (한 번만!)
    val activity = LocalContext.current as ComponentActivity
    val sharedCognitiveViewModel: CognitiveViewModel = viewModel(
        viewModelStoreOwner = activity
    )


    //타이틀 명지정
    val topTitle = when (currentRoute) {
        Routes.HOME -> "홈"
        Routes.COGNITIVE -> "인지 능력 검사"
        Routes.COGNITIVE_TEST -> "인지 능력 검사"
        Routes.COGNITIVE_WAITING -> "검사 결과 대기"
        Routes.COGNITIVE_RESULT -> "검사 결과"
        Routes.DIARY -> "오늘의 일기"
        Routes.LEARN -> "오늘의 학습"
        Routes.MY -> "내 정보"
        Routes.DAY_SUMMARY -> "하루요약"
        Routes.DAY_SUMMARY_WITH_ARG -> "하루요약"
        Routes.NUMBERS_GAME -> "숫자게임"
        else -> ""
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(topTitle) }) },
        bottomBar = {
            HaruBottomBar(
                currentRoute = currentRoute,
                onSelect = { route ->
                    nav.navigate(route) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            //하단 홈 버튼
            composable(Routes.HOME) { HomeScreen(onNavigate = { nav.navigate(it) }) }
            //홈화면 인지능력검사
            composable(Routes.COGNITIVE) { CognitiveIntroScreen(
                onStart = { nav.navigate(Routes.COGNITIVE_TEST) },
                viewModel = sharedCognitiveViewModel  // 🔥 추가!
            ) }
            //인지능력 검사 화면
            composable(Routes.COGNITIVE_TEST) { CognitiveTestScreen(nav, sharedCognitiveViewModel) }
            // 인지능력 검사
            composable(Routes.COGNITIVE_WAITING) { CognitiveWaitingScreen(navController = nav, sharedCognitiveViewModel) }
            composable(Routes.COGNITIVE_RESULT) {
                CognitiveResultScreen(navController = nav, sharedCognitiveViewModel)
            }
            //오늘의 일기
            composable(Routes.DIARY) {
                //SimplePage("오늘의 일기")
                DiaryScreen(nav,onCancel = { nav.popBackStack() })
            }
            //오늘의 학습
            composable(Routes.LEARN) {
                //SimplePage("오늘의 학습")
            }
            //마이페이지
            composable(Routes.MY) {
                ProfileScreen()
            }
            //하루요약
            composable(
                route = Routes.DAY_SUMMARY_WITH_ARG, // 인자를 받는 경로로 변경
                arguments = listOf(navArgument("aiFeedbackResultJson") {
                    type = NavType.StringType
                    nullable = true // null 값도 허용
                })
            ) { backStackEntry ->
                val aiFeedbackResultJson = backStackEntry.arguments?.getString("aiFeedbackResultJson")
                val aiFeedbackResult: ResultAiDiary? = if (aiFeedbackResultJson != "null" && aiFeedbackResultJson != null) {
                    val decodedJson = URLDecoder.decode(aiFeedbackResultJson, StandardCharsets.UTF_8.toString())
                    Gson().fromJson(decodedJson, ResultAiDiary::class.java)
                } else {
                    null
                }
                DaySummary(navController = nav, aiFeedbackResult = aiFeedbackResult)
            }
            //숫자게임
            composable(Routes.NUMBERS_GAME) {
                NumbersGameScreen(nav)
            }
        }
    }
}

@Composable
private fun HaruBottomBar(
    currentRoute: String?,
    onSelect: (String) -> Unit
) {
    if (currentRoute != Routes.COGNITIVE_TEST ){
        if (currentRoute != Routes.COGNITIVE_WAITING ){
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute in listOf(Routes.HOME, Routes.COGNITIVE, Routes.DIARY, Routes.LEARN),
                    onClick = { onSelect(Routes.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "홈") },
                    label = { Text("홈") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.MY,
                    onClick = { onSelect(Routes.MY) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "마이") },
                    label = { Text("마이") }
                )
            }
        }
    }
}

@Composable
private fun NavHostController.currentRoute(): String? {
    val backStackEntry by currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}
