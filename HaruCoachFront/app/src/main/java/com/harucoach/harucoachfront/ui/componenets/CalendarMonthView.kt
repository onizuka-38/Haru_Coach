package com.harucoach.harucoachfront.ui.componenets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harucoach.harucoachfront.R
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

/**
 * CalendarMonthView:
 * - month: 보여주려는 연월(YearMonth)
 * - selected: 현재 선택된 날짜(LocalDate)
 * - onSelectDate: 사용자가 날짜를 눌렀을 때 호출되는 함수
 * - today: 오늘 날짜(오늘 표시용)
 * - moodForDay: 특정 날짜에 표시할 이모지를 알려주는 함수
 */
@Composable
fun CalendarMonthView(
    month: YearMonth,                              // 이 칸은 "보여줄 달" 정보를 받는 자리예요. (예: 2025-11)
    selected: LocalDate,                           // 사용자가 지금 선택한 날짜예요.
    onSelectDate: (LocalDate) -> Unit,             // 사용자가 달력의 날짜를 누르면 이 함수를 불러요.
    today: LocalDate,                              // 오늘 날짜를 알려줘요 (오늘 표시용)
    moodForDay: (LocalDate) -> String?             // 날짜를 넣으면 그 날짜의 이모지를 돌려주는 함수예요.
) { // start CalendarMonthView

    // 1) 이번 달의 1일을 가져와요. (예: 2025-11-01)
    val first = month.atDay(1)

    // 2) 이번 달의 마지막 날을 가져와요. (예: 2025-11-30)
    val last = month.atEndOfMonth()

    // 3) 이번 달 1일이 무슨 요일인지 숫자로 변환해요.
    //    dayOfWeek.value는 월요일=1 ... 일요일=7 이라서, 일요일을 0으로 맞추려면 %7을 해요.
    val startDow = (first.dayOfWeek.value % 7)

    // 4) 이번 달의 총 날 수(몇일까지 있는지) 가져와요.
    val totalDays = last.dayOfMonth

    // 5) 달력의 칸들을 만들거예요. 빈칸(null)과 날짜(LocalDate)를 섞어서 리스트로 만듭니다.
    //    예: [null, null, 1일, 2일, 3일, ...]
    val cells = buildList<LocalDate?> {
        // 5-a) 달력 첫 주의 앞부분을 빈칸으로 채워서 요일을 맞춰요.
        repeat(startDow) { add(null) }                 // 예: startDow가 2면 null,null 추가

        // 5-b) 1일부터 마지막 날까지 한 칸씩 추가해요.
        for (d in 1..totalDays) add(month.atDay(d))    // 예: add(2025-11-01), add(2025-11-02) ...
    } // end cells

    // 6) 달력 전체를 카드 모양으로 감싸서 모서리와 그림자를 줘요.
    Card(
        modifier = Modifier
            .fillMaxWidth()                           // 가로 전체를 사용
            .clip(RoundedCornerShape(16.dp)),         // 모서리를 둥글게
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) { // start Card month
        // 7) 카드 안의 내용은 세로로 차곡차곡 쌓을 거예요.
        Column(modifier = Modifier.padding(12.dp)) { // start Column

            // 8) 요일 헤더: "일 월 화 수 목 금 토" 를 한 줄로 보여줘요.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach { dayLabel ->
                    Text(
                        text = dayLabel,                 // 요일 글자
                        modifier = Modifier.weight(1f),  // 각 칸이 같은 넓이를 갖도록 함
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center     // 글자를 칸 가운데로 정렬
                    )
                }
            } // end Row weekday header

            Spacer(modifier = Modifier.height(6.dp))    // 요일과 날짜 사이에 빈 공간

            // 9) 날짜들을 7개씩 묶어서 여러 줄로 보여주기 위해 줄(row) 수를 계산해요.
            val rows = kotlin.math.ceil(cells.size / 7.0).toInt()

            // 10) 각 줄을 돌면서 내부의 7칸을 그려요.
            Column { // start calendar rows
                for (r in 0 until rows) { // 줄마다 반복
                    Row(modifier = Modifier.fillMaxWidth()) { // start week row
                        for (c in 0..6) { // 한 줄에 7칸: 0 ~ 6
                            val index = r * 7 + c                 // 리스트에서 실제 인덱스 계산
                            val cellDate = cells.getOrNull(index) // null이면 빈칸, 아니면 날짜

                            // 11) 각 칸은 정사각형 모양(Box)으로 만들고 가운데 정렬합니다.
                            Box(
                                modifier = Modifier
                                    .weight(1f)            // 칸 넓이 균등 분배
                                    .aspectRatio(1f)      // 정사각형 유지
                                    .padding(4.dp)        // 칸 안쪽 여백
                                    .clickable(enabled = cellDate != null) {
                                        // 12) 칸을 클릭하면 날짜가 null이 아닐 때만 onSelectDate 호출해요.
                                        cellDate?.let { onSelectDate(it) }
                                    },
                                contentAlignment = Alignment.Center
                            ) { // start Box for cell

                                // 13) 만약 칸이 날짜(실제 날짜)가 아니라면(앞/뒤 빈칸) 아무것도 안그립니다.
                                if (cellDate == null) {
                                    // 빈칸이므로 아무 것도 그리지 않음
                                } else {
                                    // 14) 날짜가 있는 칸: 선택 여부와 오늘 여부를 계산해요.
                                    val isSelected = cellDate == selected   // 사용자가 고른 날짜인지
                                    val isToday = cellDate == today        // 오늘 날짜인지

                                    // 15) 그 날짜에 연결된 이모지(기분)가 있는지 moodForDay 함수로 알아봐요.
                                    //     moodForDay(cellDate)가 null이면 이 날에 일기가 없다는 뜻이에요.
                                    val mood = moodForDay(cellDate)        // 예: "😊" 또는 null

                                    // 16) 중앙 정렬된 세로 컬럼(위아래로 쌓음)으로 날짜 내용 표시
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) { // start Column for cell content

                                        // 17) 핵심 동작:
                                        //     - 만약 그날의 mood(이모지)가 있으면 숫자 대신 **이모지만** 보여줘요.
                                        //     - mood가 없으면 원래처럼 날짜 숫자를 보여줘요.
                                        if (mood != null) {
                                            // 이모지가 있으면 이모지만 보여줍니다.
                                            /*Text(
                                                text = mood,               // 예: "😊"
                                                fontSize = 20.sp,          // 조금 더 크게 보여줌
                                                modifier = Modifier.padding(4.dp)
                                            )*/
                                            Image(
                                                painter = painterResource(id = when(mood) {
                                                    "행복함"-> R.drawable.happiness
                                                    "보통" -> R.drawable.normal_feelings
                                                    "우울함"-> R.drawable.depressed
                                                    "화남"-> R.drawable.aggro
                                                    "차분함"-> R.drawable.calm
                                                    "생각중"-> R.drawable.thinking
                                                    "설렘"-> R.drawable.excitement
                                                    "피곤함"-> R.drawable.tired
                                                    "아픔"-> R.drawable.pain
                                                    "고마움"-> R.drawable.thanks
                                                    else -> R.drawable.upset
                                                }),
                                                contentDescription = mood,
                                                modifier = Modifier.size(20.dp) // 이미지 크기 조절
                                            )
                                        } else {
                                            // 이모지가 없으면 날짜 숫자를 보여줍니다.
                                            Text(
                                                text = cellDate.dayOfMonth.toString(), // 예: "8"
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = if (isSelected)
                                                    MaterialTheme.colorScheme.onPrimary
                                                else
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        } // end if mood

                                        // 18) 만약 이 날짜가 '오늘'이면 작은 점(●)을 아래에 보여줘요.
                                        if (isToday && !isSelected) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("●", style = MaterialTheme.typography.bodySmall)
                                        }

                                    } // end Column for cell content
                                } // end else cellDate != null
                            } // end Box for cell
                        } // end for c in 0..6
                    } // end Row for week
                } // end for r in 0 until rows
            } // end Column for calendar rows
        } // end Column inside Card
    } // end Card month
} // end CalendarMonthView
