plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.harucoach.harucoachfront"
    compileSdk {
        version = release(35)
    }

    defaultConfig {
        applicationId = "com.harucoach.harucoachfront"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")  // Retrofit for API calls (호환 버전: Kotlin 2.0과 Compose 2024.09 OK)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")  // JSON 파싱
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")//

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")  // ViewModel과 Compose 통합 (기존 lifecycleRuntimeKtx 2.6.1 업그레이드 추천)

    implementation("androidx.datastore:datastore-preferences:1.1.2") // datastore


    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Hilt를 Compose ViewModel과 사용하기 위함
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Accompanist Pager (horizontal swipe pager)
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")

    //    헬스커넥트 API
    implementation("androidx.health.connect:connect-client:1.1.0-beta01")

}

// kapt { ... } 블록이 없다면 파일 하단에 추가
kapt {
    correctErrorTypes = true
}