package com.atcumt.kxq.utils.network.post.news

import com.atcumt.kxq.utils.network.ApiServiceS
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Locale

// DateFormatter.kt
object DateFormatter {
    private val newsFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA)

    fun newsParse(isoDate: String): String {
        return try {
            val date = newsFormat.parse(isoDate)
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(date)
        } catch (e: Exception) {
            isoDate // 解析失败时返回原始字符串
        }
    }
}

class NewsListService {

    // NewsListResponse.kt
    data class NewsListResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: NewsListData?
    )

    data class NewsListData(
        @SerializedName("newsCategory") val newsCategory: String?,
        @SerializedName("newsType") val newsType: String?,
        @SerializedName("sourceName") val sourceName: String?,
        @SerializedName("size") val size: Int?,
        @SerializedName("cursor") val cursor: String?,
        @SerializedName("lastNewsId") val lastNewsId: Double?,
        @SerializedName("newsList") val newsList: List<NewsItem>?
    )

    data class NewsItem(
        @SerializedName("newsId") val newsId: Double?,
        @SerializedName("newsCategory") val newsCategory: String?,
        @SerializedName("newsType") val newsType: String?,
        @SerializedName("sourceName") val sourceName: String?,
        @SerializedName("shortName") val shortName: String?,
        @SerializedName("sourceUrl") val sourceUrl: String?,
        @SerializedName("showType") val showType: String?,
        @SerializedName("title") val title: String?,
        @SerializedName("images") val images: List<String>?,
        @SerializedName("commentCount") val commentCount: Int?,
        @SerializedName("viewCount") val viewCount: Int?,
        @SerializedName("status") val status: String?,
        @SerializedName("score") val score: Double?,
        @SerializedName("publishTime") val publishTime: String?
    ) {
        // 扩展属性
        val publishTimeFormatted: String?
            get() = publishTime?.let { DateFormatter.newsParse(it) }

        val imageUrl: String?
            get() = images?.firstOrNull()

        val source: String?
            get() = shortName?.let {
                sourceName?.let { name -> "$it·$name" } ?: it
            }
    }

    // 本地模拟数据
    private val localResponse =
        """
        {
        "code": 200,
        "msg": "成功",
        "data": {
        "newsCategory": "校园",
        "newsType": "中国矿业大学新闻网",
        "sourceName": "视点新闻",
        "size": 10,
        "cursor": "2025-01-01T20:01:27",
        "lastNewsId": 1884938914562674714,
        "newsList": [
            {
                "newsId": 1884938914562674727,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71704.htm",
                "showType": "direct",
                "title": "我校南京校友会举行校友经济创新发展论坛",
                "images": [
                    "https://news.cumt.edu.cn/__local/0/9D/F7/D7B0E78536D7B3E3322E2FCC26A_5051DC19_1B3EE.jpg"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-06T20:01:26"
            },
            {
                "newsId": 1884938914562674726,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71703.htm",
                "showType": "direct",
                "title": "我校黑龙江校友会举办智慧矿山及新能源建设研讨会",
                "images": [
                    "https://news.cumt.edu.cn/__local/5/56/87/AF65E50DBF8A5F49A7A9D684E39_9C135E29_241D4.jpg"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-06T20:01:26"
            },
            {
                "newsId": 1884938914562674725,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71702.htm",
                "showType": "direct",
                "title": "学校召开校领导班子成员2024年度民主生活会学习研讨暨党委理论学习中心组集中学习会",
                "images": [
                    "https://news.cumt.edu.cn/__local/D/F9/CD/B67D40192E3D842CFEFC1CFED58_96FB4B76_20DDD.jpg"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-06T20:01:26"
            },
            {
                "newsId": 1884938914562674723,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71699.htm",
                "showType": "direct",
                "title": "我校开展妇女工作评优总结迎新座谈",
                "images": [
                    "https://news.cumt.edu.cn/__local/2/D2/BE/C1A430511C21C67CCC98F82F1FB_870870B3_1208A0.jpg"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-03T20:01:25"
            },
            {
                "newsId": 1884938914562674722,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71698.htm",
                "showType": "direct",
                "title": "我校主办3种科技期刊入选2024年度“国际影响力TOP期刊”榜单",
                "images": null,
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-03T20:01:25"
            },
            {
                "newsId": 1884938914562674721,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71701.htm",
                "showType": "direct",
                "title": "中国矿业大学2024年十大新闻权威发布",
                "images": [
                    "https://news.cumt.edu.cn/__local/5/0C/79/E91197671B56C737AD599B4E756_C4F68B27_5E4DC.png"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-03T20:01:25"
            },
            {
                "newsId": 1884938914562674719,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71688.htm",
                "showType": "direct",
                "title": "深地工程智能建造与健康运维全国重点实验室主任 谢和平院士CO2捕集全新原理技术成果登上Nature子刊",
                "images": [
                    "https://news.cumt.edu.cn/__local/F/2D/27/6BF3CBB01A39352455AC5F0A01E_315A20C3_2B9E5.jpg"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-02T20:01:27"
            },
            {
                "newsId": 1884938914562674718,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71690.htm",
                "showType": "direct",
                "title": "学校举办“逐梦2025”中国矿业大学教职工迎新年健步行活动",
                "images": [
                    "https://news.cumt.edu.cn/__local/2/AB/C2/6FA5D6519801C3506D39E31409A_A720F790_33EE0.jpg"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-02T20:01:27"
            },
            {
                "newsId": 1884938914562674717,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71691.htm",
                "showType": "direct",
                "title": "我校文昌校园两栋建筑入选徐州市历史建筑保护名录",
                "images": null,
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-02T20:01:25"
            },
            {
                "newsId": 1884938914562674714,
                "newsCategory": "校园",
                "newsType": "中国矿业大学新闻网",
                "shortName": "新闻网",
                "sourceName": "视点新闻",
                "sourceUrl": "https://news.cumt.edu.cn/info/1002/71683.htm",
                "showType": "direct",
                "title": "我校举行2025年元旦嘉年华系列活动",
                "images": [
                    "https://news.cumt.edu.cn/__local/D/04/06/9938BBD0891E289E152E2982289_B6A30967_433CB.jpg"
                ],
                "commentCount": 0,
                "viewCount": 0,
                "status": "PUBLISHED",
                "score": 0.0,
                "publishTime": "2025-01-01T20:01:27"
            }
        ]
        }
        }
        """

    // 获取新闻列表（异步）
    fun getNewsList(
        callback: (NewsListResponse?, Throwable?) -> Unit
    ) {
        ApiServiceS.get(
            baseUrl = ApiServiceS.BASE_URL_POST,
            endpoint = "news/v1",
            headers = mapOf("Accept" to "application/json")
        ) { response, error ->
            if (error != null) {
                // 网络失败时返回本地数据
                val localData = parseNewsResponse(localResponse)
                callback(localData, null)
            } else {
                // 解析网络响应
                val parsed = response?.let { parseNewsResponse(it) }
                callback(parsed, null)
            }
        }
    }

    // 获取新闻列表（协程挂起）
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getNewsListBlocking(): NewsListResponse {
        return suspendCancellableCoroutine { cont ->
            getNewsList { response, error ->
                if (error != null) {
                    val localData = parseNewsResponse(localResponse)
                    cont.resume(localData!!, null)
                } else {
                    cont.resume(response!!, null)
                }
            }
        }
    }

    // 解析响应
    private fun parseNewsResponse(json: String): NewsListResponse? {
        return try {
            Gson().fromJson(json, NewsListResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}