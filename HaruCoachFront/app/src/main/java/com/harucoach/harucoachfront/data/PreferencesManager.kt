package com.harucoach.harucoachfront.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class PreferencesManager(private val context: Context) {
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    // 토큰 읽기
    val authTokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[AUTH_TOKEN_KEY] } // end authTokenFlow

    // 토큰 저장
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN_KEY] = token
        }
    }// end saveAuthToken

    // 토큰 제거 (로그아웃)
    suspend fun clearAuthToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN_KEY)
        }
    }// end clearAuthToken


    /**
     * 앱 시작 시 한 번만(동기적으로) 토큰을 읽어야 할 때 사용.
     * 주의: runBlocking으로 메인 스레드를 잠시 블록함 — 짧게 읽는 용도로만 사용하세요.
     */
    fun readAuthTokenBlocking(): String? {
        return try {
            runBlocking {
                context.dataStore.data
                    .map { prefs -> prefs[AUTH_TOKEN_KEY] } // null 가능
                    .first()
            }
        } catch (e: Exception) {
            null
        }
    }// end readAuthTokenBlocking

}// end PreferencesManager

