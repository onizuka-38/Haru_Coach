package com.harucoach.harucoachfront

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harucoach.harucoachfront.data.PreferencesManager
import com.harucoach.harucoachfront.ui.screens.HomeScreen
import com.harucoach.harucoachfront.ui.screens.LoginScreen
import com.harucoach.harucoachfront.ui.screens.LoginScreen2
import com.harucoach.harucoachfront.ui.theme.HaruCoachFrontTheme
import dagger.hilt.android.AndroidEntryPoint
import com.harucoach.harucoachfront.R // Added R import
import com.harucoach.harucoachfront.ui.screens.HaruApp // Import HaruApp
import com.harucoach.harucoachfront.ui.screens.InfiniteAnimation


@AndroidEntryPoint // 2. Hilt가 의존성을 주입할 진입점임을 선언
class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // 권한이 허용되면 토스트 메시지 표시
                    Toast.makeText(this, "녹음 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else {
                    // 권한이 거부되면 토스트 메시지 표시
                    Toast.makeText(this, "녹음 권한이 거부되었습니다. 음성 인식을 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
                }
            }
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        // 토큰 있으면 홈 시작
        val prefsManager = PreferencesManager(this)
        val token = prefsManager.readAuthTokenBlocking()
        val startDestination = if (token != null && token.isNotEmpty()) "haruApp" else "login"


        /** ---- 나눔스퀘어 폰트 지정 ---- */
        val nanumSquareNeo = FontFamily(
            Font(R.font.nanum_square_neo_variable, FontWeight.Normal)
        )

        /** ---- 나눔스퀘어 Typography ---- */
        val appTypography = Typography(
            titleLarge = TextStyle(
                fontFamily = nanumSquareNeo,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = nanumSquareNeo,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = nanumSquareNeo,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            labelLarge = TextStyle(
                fontFamily = nanumSquareNeo,
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
                typography = appTypography,  // 👈 나눔스퀘어 적용!
                content = content
            )
        }


        setContent {
            AppTheme{
                HaruCoachFrontTheme {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = startDestination) {

                        composable("login") {
                            LoginScreen2(

                                //로그인화면 듣어가기 버튼 클릭이벤트
                                onLoginClick = {
                                navController.navigate("haruApp") },
                                //로그인화면 함께해요 버튼 클릭이벤트

                                //회원가입 화면으로 이동예정
                                onJoinClick = {

                                }

                            )

                        }
                        composable("haruApp") { HaruApp() }
                    }
                } // end HaruCoachFrontTheme
            }// end setContent
        }
    }
}
