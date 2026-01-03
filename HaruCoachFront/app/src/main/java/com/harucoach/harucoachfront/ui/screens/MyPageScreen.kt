@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.harucoach.harucoachfront.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harucoach.harucoachfront.R

//ProfileScreen(nickname = "닉네임 님")


/* ===== Screen ===== */
@Composable
fun ProfileScreen(
    nickname: String = "닉네임 님",
    versionLabel: String = "버전 1.0.0",
    onEdit: () -> Unit = {
        //회원 정보 수정 버튼이벤트
    },
    onFaq: () -> Unit = {
        //FAQ 버튼이벤트
    },
    onNotice: () -> Unit = {
        //공지사항 버튼이벤트
    },
    onInquiry: () -> Unit = {
        // 1대1 문의 사항 버튼 이벤트
    }
) {
    val profileIcon   = R.drawable.profile
    val infoIcon      = R.drawable.info
    val lifebuoyIcon  = R.drawable.lifebuoy
    val bellringIcon  = R.drawable.bellring
    val clipboardIcon = R.drawable.clipboard

    Scaffold(

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            /* 프로필 헤더 */
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3AA85B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = profileIcon),
                            contentDescription = "프로필",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = nickname,
                        fontSize = 31.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 30.sp
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            /* 4개 항목: 라인으로만 구분 */
            FlatMenuList(
                items = listOf(
                    MenuItem(infoIcon, "회원 정보 수정", onEdit),
                    MenuItem(lifebuoyIcon, "FAQ", onFaq),
                    MenuItem(bellringIcon, "공지사항", onNotice),
                    MenuItem(clipboardIcon, "1 : 1 문의", onInquiry)
                )
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = versionLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = Color(0xFF9AA4B2),
                fontSize = 16.sp
            )
        }
    }
}

data class MenuItem(
    val iconRes: Int,
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun FlatMenuList(
    items: List<MenuItem>,
    inset: Dp = 20.dp,
    rowHeight: Dp = 72.dp,
    dividerColor: Color = Color(0xFFE6E8EC)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = inset)
    ) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .clickable { item.onClick() }
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (index != items.lastIndex) {
                Divider(color = dividerColor, thickness = 1.dp)
            }
        }
    }
}

