package com.atcumt.kxq.page.news.view.recommend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atcumt.kxq.page.component.FlyText
import com.atcumt.kxq.page.component.SlidingTabScreen
import com.atcumt.kxq.page.news.component.NewsListCard
import com.atcumt.kxq.utils.network.post.news.NewsListService

@Composable
fun RecommendPage() {
    Column {
        SlidingTabScreen(
            list = listOf("推荐", "热门", "最新", "推荐", "热门", "最新", "推荐", "热门", "最新")
        ) {
            repeat(10) {
                LazyColumn {
                    items(20) {
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
            }
        }
    }
}