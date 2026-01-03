package com.harucoach.harucoachfront.data


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore

// 전체 앱에서 한 번만 선언
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_refs")