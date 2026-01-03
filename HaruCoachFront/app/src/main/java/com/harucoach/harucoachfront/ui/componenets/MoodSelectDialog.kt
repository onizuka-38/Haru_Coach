package com.harucoach.harucoachfront.ui.componenets

import com.harucoach.harucoachfront.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color // Color import
import androidx.compose.foundation.background // background import


data class MoodItem(val text: String, val drawableResId: Int)

/**
 * MoodSelectDialog:
 * - 현재 감정(current)을 보여주고,
 * - 사용자가 고르면 onSelect 감정이 호출됩니다.
 * - 닫기 버튼을 누르면 onDismiss가 호출됩니다.
 */
@Composable
fun MoodSelectDialog(current: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    // start MoodSelectDialog
    // 감정 목록을 만듭니다.

    val moods = listOf(
        MoodItem("행복함", R.drawable.happiness),
        MoodItem("보통", R.drawable.normal_feelings),
        MoodItem("우울함", R.drawable.depressed),
        MoodItem("화남", R.drawable.aggro),
        MoodItem("차분함", R.drawable.calm),
        MoodItem("생각중", R.drawable.thinking),
        MoodItem("설렘", R.drawable.excitement),
        MoodItem("피곤함", R.drawable.tired),
        MoodItem("아픔", R.drawable.pain),
        MoodItem("고마움", R.drawable.thanks),
        MoodItem("속상함", R.drawable.upset)
    )
    // AlertDialog는 가운데 뜨는 작은 창입니다.
    AlertDialog(
        onDismissRequest = onDismiss, // 바깥을 눌러도 닫힐 수 있게 함
        confirmButton = { // 닫기 버튼: 누르면 onDismiss 호출
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        },
        title = { Text("오늘의 기분") }, // 다이얼로그 제목
        text = {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())) { // Column의 배경색 설정 제거
                // 감정들을 가로로 1개씩 묶어서 보여줍니다.
                moods.chunked(1).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        row.forEach { moodItem ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onSelect(moodItem.text) } // 누르면 onSelect 호출
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = moodItem.drawableResId),
                                    contentDescription = moodItem.text,
                                    modifier = Modifier.size(48.dp) // 이미지 크기 조절
                                )
                                Text(
                                    text = moodItem.text,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp) // 이미지와 텍스트 사이 간격
                                )
                            }
                        }
                    }
                }// end moods
            }// end Column
        }// end text
    )// end AlertDialog
} // end MoodSelectDialog


@Preview
@Composable
fun previewMoodSelectModal() {
    // `current`는 이제 drawable 리소스 ID를 직접적으로 참조하지 않고, 텍스트로만 전달됩니다.
    MoodSelectDialog(current = "행복함", onDismiss = {}, onSelect = {})
}