package com.harucoach.harucoachfront.ui.screens

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HealthConnectManager(private val context: Context) {
    //    생성자에서 Context타입의 값을 받아서 클래스안에 context라는 이름으로 읽기 전용(private val)로 저장함(val은 변경할 수 없는 값)
//   Context는 안드로이드에서 현재 앱,화면,컴포넌트에 대한 정보
//    HealthConnectClient는 앱에서 헬스커넥트 기능을 사용하기 위한 객체
//    내 휴대폰에 있는 헬스커넥트 앱과 연결해서 클라이언트를 만드는 코드
    val healthConnectClient = HealthConnectClient.getOrCreate(context)

    val permissions = setOf(
        //HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        // HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        //HealthPermission.getReadPermission(DistanceRecord::class),
        //HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        //HealthPermission.getReadPermission(SleepSessionRecord::class),
    )

    // 걸음수 데이터를 읽는 함수
    suspend fun readStepCounts(): List<StepsRecord> {
        val now = Instant.now()
        val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant() // 오늘 자정 시간 기준

        val request = ReadRecordsRequest(
            recordType = StepsRecord::class, // 걸음수 데이터를 읽음
            timeRangeFilter = TimeRangeFilter.between(todayStart, now) // 시간 범위 지정
        )
        return healthConnectClient.readRecords(request).records
    } //즉 readStepCounts()가 실행되면 헬스커넥트로 부터 걸음수데이터를 받아와 List<StepsRecord>에 담기는 거

}




