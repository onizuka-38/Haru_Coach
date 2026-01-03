package com.harucoach.harucoachfront.di

import android.content.Context
import com.harucoach.harucoachfront.data.PreferencesManager
import com.harucoach.harucoachfront.data.remote.ApiService
import com.harucoach.harucoachfront.data.remote.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // 앱 전역에서 사용되는 모듈
object AppModule {

    @Provides
    @Singleton // 앱 전체에서 ApiService는 단 하나만 생성
    fun provideApiService(): ApiService {
        // RetrofitInstance를 통해 ApiService 생성
        return RetrofitInstance.api
    }

    @Provides
    @Singleton // 앱 전체에서 PreferencesManager는 단 하나만 생성
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        // Hilt가 앱의 Context를 주입해줌
        return PreferencesManager(context)
    }

    // AuthRepository는 @Inject constructor를 사용했으므로 Hilt가 알아서 만듭니다.
    // (ApiService와 PreferencesManager를 여기서 제공받아서)
}