package com.atcumt.kxq.page.news.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp
import com.atcumt.kxq.utils.network.post.news.NewsListService

/**
 * [功能说明] 单条新闻卡片，与 SwiftUI NewsCardView 对应
 *
 * @param news      新闻项数据，来自 NewsListService.NewsItem（必要）:contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
 * @param modifier  外部布局修饰符（可选）
 */
@Composable
fun NewsListCard(
    news: NewsListService.NewsItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.wdp, vertical = 8.hdp).height(70.hdp),
        horizontalArrangement = Arrangement.spacedBy(9.wdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：标题 + 时间·来源·浏览数
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 标题，限两行
            Text(
                text = news.title.orEmpty(),
                maxLines = 2,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.ssp,
                    color = FlyColors.FlyText
                )
            )
            // 时间、来源、浏览数
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //只取字段T前面的
                Text(
                    text = news.publishTimeFormatted
                        ?.takeIf { it.isNotEmpty() }?.substring(0, 10)
                        .orEmpty(),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.ssp,
                        color = FlyColors.FlyTextGray
                    )
                )
                news.source?.let { src ->
                    Text(
                        text = src,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.ssp,
                            color = FlyColors.FlyTextGray
                        )
                    )
                }
                Text(
                    text = "浏览 ${news.viewCount ?: 0}",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.ssp,
                        color = FlyColors.FlyTextGray
                    )
                )
            }
        }

        // 右侧：图片或占位
        val imageModifier = Modifier
            .size(width = 89.wdp, height = 70.hdp)
            .clip(RoundedCornerShape(8.wdp))

        if (!news.imageUrl.isNullOrEmpty()) {
            // Coil 异步加载网络图
            AsyncImage(
                model = news.imageUrl,
                contentDescription = news.title,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
                placeholder = rememberAsyncImagePainter(
                    model = news.imageUrl,
                    placeholder = ColorPainter(FlyColors.FlySecondaryBackground)
                )
            )
        } else {
            // 无图占位，叠加首字作为提示
            Box(
                modifier = imageModifier
                    .background(FlyColors.FlySecondaryBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = news.title
                        ?.takeIf { it.isNotEmpty() }
                        ?.first()
                        ?.toString()
                        .orEmpty(),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.ssp,
                        color = FlyColors.FlyBackground
                    )
                )
            }
        }
    }
}

/**
 *             {
 *                 "newsId": 1884938914562674727,
 *                 "newsCategory": "校园",
 *                 "newsType": "中国矿业大学新闻网",
 *                 "shortName": "新闻网",
 *                 "sourceName": "视点新闻",
 *                 "sourceUrl": "https://news.cumt.edu.cn/info/1002/71704.htm",
 *                 "showType": "direct",
 *                 "title": "我校南京校友会举行校友经济创新发展论坛",
 *                 "images": [
 *                     "https://news.cumt.edu.cn/__local/0/9D/F7/D7B0E78536D7B3E3322E2FCC26A_5051DC19_1B3EE.jpg"
 *                 ],
 *                 "commentCount": 0,
 *                 "viewCount": 0,
 *                 "status": "PUBLISHED",
 *                 "score": 0.0,
 *                 "publishTime": "2025-01-06T20:01:26"
 *             },
 */
@Preview
@Composable
fun NewsListCardPreview() {
    KxqTheme {
        NewsListCard(
            news = NewsListService.NewsItem(
                newsId = 1.88493891456267469E18,
                newsCategory = "校园",
                newsType = "中国矿业大学新闻网",
                shortName = "新闻网",
                sourceName = "视点新闻",
                sourceUrl = "https://news.cumt.edu.cn/info/1002/71704.htm",
                showType = "direct",
                title = "我校南京校友会举行校友经济创新发展论坛",
                images = listOf(
                    "https://news.cumt.edu.cn/__local/0/9D/F7/D7B0E78536D7B3E3322E2FCC26A_5051DC19_1B3EE.jpg"
                ),
                commentCount = 0,
                viewCount = 0,
                status = "PUBLISHED",
                score = 0.0,
                publishTime = "2025-01-06T20:01:26"
            )
        )
    }
}