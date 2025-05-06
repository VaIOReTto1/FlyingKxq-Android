package com.atcumt.kxq.utils

import com.atcumt.kxq.R
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * 规范化新闻时间展示：
 * - 今天：只显示“HH:mm”
 * - 昨天：前缀“昨天 ” + “HH:mm”
 * - 本年：显示“M月d日”
 * - 往年：显示“yyyy-MM-dd HH:mm”
 *
 * 通过依赖注入 Clock 与 StringProvider 保证可测试、可国际化、解耦 UI/资源。
 */
@ActivityRetainedScoped
class FlyDateFormatter @Inject constructor(
    private val clock: Clock,                            // 可注入 FakeClock 进行单元测试
    private val stringProvider: StringProvider           // 国际化文本提供者
) {
    // 线程安全、可复用的 DateTimeFormatter
    private val parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val monthDayFormatter = DateTimeFormatter.ofPattern("M月d日")
    private val fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    /**
     * 将 ISO-8601 时间字符串（如 "2025-05-06T14:30:00"）格式化为新闻展示格式。
     * 出错则返回空串。
     */
    fun parseNewsDate(dateString: String): String = runCatching {
        // 1. 解析输入
        val localDateTime = LocalDateTime.parse(dateString, parseFormatter)
        val dateTime = localDateTime.atZone(ZoneId.systemDefault())
        val now = ZonedDateTime.now(clock)

        // 2. 分支格式化
        when {
            dateTime.toLocalDate().isEqual(now.toLocalDate()) ->
                dateTime.format(timeFormatter)                              // “HH:mm”

            dateTime.toLocalDate().isEqual(now.toLocalDate().minusDays(1)) ->
                stringProvider.getString(
                    R.string.yesterday_format,
                    dateTime.format(timeFormatter)
                )                                                          // “昨天 HH:mm”

            dateTime.year == now.year ->
                dateTime.format(monthDayFormatter)                         // “M月d日”

            else ->
                dateTime.format(fullFormatter)                             // “yyyy-MM-dd HH:mm”
        }
    }.getOrDefault("")                                                      // 异常时返回空
}