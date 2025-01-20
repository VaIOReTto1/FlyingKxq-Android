package com.atcumt.kxq.page.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.atcumt.kxq.R
import com.atcumt.kxq.page.component.FlyButton.FlyWeakenButton
import com.atcumt.kxq.page.component.FlyTabRow
import com.atcumt.kxq.page.component.FlyText.AppbarTitle
import com.atcumt.kxq.page.component.FlyText.SignalText
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.utils.wdp

@Composable
fun ProfilePage() {
    val profileFolders = listOf("消息", "评论", "帖子", "收藏")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { profileFolders.size })
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ProfileHeaders()
        FlyTabRow(profileFolders, pagerState, 393.wdp,34.wdp)
        HorizontalPagerContent(pagerState)
    }
}
/**
 * 页面内容区域
 * @param pagerState Pager 的状态
 * @param noteFolders Tab 对应的页面内容
 */
@Composable
private fun HorizontalPagerContent(
    pagerState: PagerState,
) {
    HorizontalPager(
        state = pagerState,
    ) { page ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        }
    }
}
@Composable
fun ProfileHeaders() {
    Column(
        modifier = Modifier
            .padding(top = 13.4.wdp, end = 34.wdp, start = 24.wdp, bottom = 32.wdp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.more_horiz),
            contentDescription = "more_horiz",
            modifier = Modifier.align(Alignment.End)
                .width(26.6.wdp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
        )
        Row(
            modifier = Modifier
                .padding(top = 33.wdp, bottom = 17.wdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // 可以替换为实际的图片资源
                contentDescription = "profile_picture",
                modifier = Modifier
                    .padding(end = 8.wdp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .width(60.wdp)
                    .height(60.wdp), // 圆形裁剪
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
            Column {
                AppbarTitle(text = "Kxq", modifier = Modifier.height(22.wdp))
                Spacer(modifier = Modifier.height(2.wdp))
                SignalText(text = "@kxq", modifier = Modifier.height(17.wdp))
            }
            Spacer(modifier = Modifier.weight(1f))
            FlyWeakenButton(
                modifier = Modifier.width(77.wdp).height(26.wdp),
                round = 13.wdp,
                width = 1.wdp,
                onClick = {},
                content = {

                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagCard(title = "标签", conten = "标签")
        }
        Row(
            modifier = Modifier.padding(vertical = 16.wdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagCard(title = "标签", conten = "标签")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagCard(title = "标签", conten = "标签")
        }
    }
}

@Composable
fun TagCard(title: String, conten: String) {
    Row(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(4.wdp)
        ).padding(start = 16.wdp, end = 16.wdp, top = 5.wdp, bottom = 4.wdp)
    ) {
        SignalText(text = title, modifier = Modifier.height(17.wdp))
        Spacer(modifier = Modifier.width(12.wdp))
        SignalText(text = conten, modifier = Modifier.height(17.wdp))
    }
}

@Preview
@Composable
fun ProfilePagePreview() {
    MaterialTheme {
        AdaptiveScreen {
            ProfilePage()
        }
    }

}