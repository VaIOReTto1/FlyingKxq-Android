package com.atcumt.kxq.page.profile

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.atcumt.kxq.R
import com.atcumt.kxq.page.component.FlyButton.FlyWeakenButton
import com.atcumt.kxq.page.component.FlyDivider
import com.atcumt.kxq.page.component.FlyTabRow
import com.atcumt.kxq.page.component.FlyText
import com.atcumt.kxq.page.component.FlyText.AppbarTitle
import com.atcumt.kxq.page.component.FlyText.SignalText
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.utils.NavViewModel
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfilePage() {
    val profileFolders = listOf("消息", "评论", "帖子", "收藏")
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { profileFolders.size })
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState, // 监听滚动状态
        modifier = Modifier.fillMaxSize()
    ) {
        // 头部区域（不固定）
        item {
            ProfileHeaders()
        }

        // 固定 FlyTabRow（吸顶效果）
        stickyHeader {
            FlyTabRow(
                folders = profileFolders,
                pagerState = pagerState,
                width = 393.wdp,
                height = 34.wdp,
                fontSize = 12.ssp
            )
        }

        // HorizontalPager 内容区域
        item {
            HorizontalPagerContent(pagerState)
        }
    }
}

/**
 * 页面内容区域
 * @param pagerState Pager 的状态
 */
@Composable
private fun HorizontalPagerContent(
    pagerState: PagerState,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        beyondViewportPageCount = 1
    ) { page ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            repeat(20) {
                CommentCard(
                    avatarUrl = "https://qlogo2.store.qq.com/qzone/1004275481/1004275481/100",
                    name = "张三",
                    content = "恭喜王老师！",
                    referenceTitle = "计算机学院2024奶奶本科教学高水平成果奖公示",
                    date = "3小时前",
                    footer = "赞 325"
                )
            }
        }
    }
}

@Composable
fun ProfileHeaders() {
    val navController = NavViewModel.navController.value
    Column(
        modifier = Modifier
            .padding(top = 13.4.wdp, end = 20.wdp, start = 24.wdp, bottom = 32.wdp),
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
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data("https://qlogo2.store.qq.com/qzone/1004275481/1004275481/100")
                        .apply {
                            crossfade(true)
                            placeholder(R.drawable.ic_launcher_foreground)
                            error(R.drawable.ic_launcher_foreground)
                            // 关键优化：缩小图片尺寸到实际显示大小
//                            size(13) // 单位：像素（根据实际需求调整）
                            // 可选：启用内存缓存
                            memoryCacheKey(
                                MemoryCache.Key("https://qlogo2.store.qq.com/qzone/1004275481/1004275481/100")
                            )
                        }.build()
                ),
                contentDescription = "avatar",
                modifier = Modifier
                    .padding(vertical = 12.wdp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .width(60.wdp)
                    .height(60.wdp), // 圆形裁剪
                contentScale = ContentScale.Crop
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
                onClick = {
                    Log.d("navC",navController.toString())
                    navController?.navigate("name")
                },
                content = {
                    FlyText(text = "编辑资料", fontSize = 12.ssp, color = FlyColors.FlyMain)
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagCard(title = "个签", content = "标签")
        }
        Row(
            modifier = Modifier.padding(vertical = 16.wdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagCard(title = "😊", content = "开心")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TagCard(title = "粉丝", content = "标签")
            Box(
                modifier = Modifier.padding(horizontal = 10.wdp)
            ) {
                TagCard(title = "关注", content = "标签")
            }
            TagCard(title = "获赞", content = "标签")
        }
    }
}

@Composable
fun TagCard(title: String, content: String) {
    Row(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(4.wdp)
        ).padding(start = 16.wdp, end = 16.wdp, top = 5.wdp, bottom = 4.wdp)
    ) {
        FlyText(
            text = title,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 12.ssp,
            fontWeight = FontWeight.W400
        )
        Spacer(modifier = Modifier.width(12.wdp))
        FlyText(
            text = content,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 12.ssp,
            fontWeight = FontWeight.W400
        )
    }
}

@Composable
fun CommentCard(
    avatarUrl: String,
    name: String,
    content: String,
    referenceTitle: String,
    date: String,
    footer: String,
//    click: () -> Void = (){}
) {
    Column(Modifier.padding(top = 10.wdp, start = 24.wdp, end = 24.wdp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .apply {
                            crossfade(true)
                            placeholder(R.drawable.ic_launcher_foreground)
                            error(R.drawable.ic_launcher_foreground)
                            // 关键优化：缩小图片尺寸到实际显示大小
                            size(13) // 单位：像素（根据实际需求调整）
                            // 可选：启用内存缓存
                            memoryCacheKey(MemoryCache.Key(avatarUrl))
                        }.build()
                ),
                contentDescription = "avatar",
                modifier = Modifier
                    .size(13.wdp) // 统一宽高
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            FlyText(name, fontSize = 12.ssp, fontWeight = FontWeight.W400)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Rounded.MoreHoriz,
                contentDescription = "favorite",
                modifier = Modifier.width(18.wdp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        FlyText(
            content,
            fontSize = 16.ssp,
            fontWeight = FontWeight.W400,
            modifier = Modifier.padding(vertical = 8.wdp)
        )
        Box(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(8.wdp)
            ).fillMaxWidth()
                .padding(start = 12.45.wdp, top = 6.wdp, end = 12.45.wdp, bottom = 7.wdp)
        ) {
            FlyText(
                referenceTitle,
                fontSize = 12.ssp,
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(vertical = 8.wdp),
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        Row(
            modifier = Modifier.padding(top = 7.wdp, bottom = 8.wdp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FlyText(
                date,
                fontSize = 12.ssp,
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(end = 4.wdp)
            )
            FlyText(
                footer,
                fontSize = 12.ssp,
            )
        }
        FlyDivider()
    }
}

@Preview
@Composable
fun ProfilePagePreview() {
    KxqTheme {
        AdaptiveScreen {
            ProfilePage()
        }
    }

}