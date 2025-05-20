package com.atcumt.kxq.page.component.FlyMarkdownTextView

import android.content.Context
import android.text.Spanned
import android.util.Log
import android.util.LruCache
import dagger.hilt.android.qualifiers.ApplicationContext
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [功能说明] Markdown解析仓库接口
 */
interface MarkdownRepository {
    /**
     * 解析Markdown文本为Spanned对象(可显示的富文本)
     * @param markdown 原始Markdown文本
     * @param isCodeBlockColorful 是否启用代码高亮
     * @return 解析后的Spanned对象
     */
    suspend fun parse(markdown: String, isCodeBlockColorful: Boolean = true): Spanned

    /**
     * 清除缓存
     */
    suspend fun clearCache()
}

/**
 * Prism4j语言定义Bundle
 * 支持常用编程语言的语法高亮
 */
@PrismBundle(
    include = [
        "java", "kotlin", "javascript", "css", "json",
        "markdown", "python", "bash", "sql", "c", "cpp"
    ],
    grammarLocatorClassName = ".GrammarLocatorImpl"
)
class MarkdownPrismLanguages

/**
 * [功能说明] 基于Markwon的Markdown解析仓库实现
 *
 * 提供:
 * - 基于Markwon的Markdown解析
 * - 代码高亮支持
 * - 内存缓存
 * - 图片加载支持
 * - 各种Markdown扩展支持
 */

@Singleton
class MarkwonMarkdownRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : MarkdownRepository {

    /** 内存缓存 - 用于暂存解析结果 */
    private val memoryCache = object : LruCache<String, Spanned>(256) {
        override fun sizeOf(key: String, value: Spanned) = value.length
    }

    /** 代码高亮实例 */
//    private val prism4j by lazy {
//        Prism4j(GrammarLocatorImpl())
//    }

    /** 默认Markwon实例 - 不包含代码高亮 */
    private val markwonBasic by lazy {
        Markwon.builder(context)
            .usePlugin(CorePlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(GlideImagesPlugin.create(context))
            .build()
    }

    /** 带语法高亮的Markwon实例 */
    private val markwonWithHighlight by lazy {
        Markwon.builder(context)
            .usePlugin(CorePlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(GlideImagesPlugin.create(context))
//            .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDefault.create()))
            .build()
    }

    /**
     * 解析Markdown为富文本
     * @param markdown 原始Markdown文本
     * @param isCodeBlockColorful 是否启用代码高亮
     * @return 解析后的Spanned对象
     */
    override suspend fun parse(markdown: String, isCodeBlockColorful: Boolean): Spanned =
        withContext(Dispatchers.IO) {
            try {
                // 检查参数有效性
                if (markdown.isBlank()) {
                    return@withContext markwonBasic.toMarkdown("")
                }

                // 生成缓存键
                val cacheKey = "${markdown.hashCode()}_$isCodeBlockColorful"

                // 检查缓存
                memoryCache[cacheKey]?.let { cached ->
                    return@withContext cached
                }

                // 解析
                val result = if (isCodeBlockColorful) {
                    val node = markwonWithHighlight.parse(markdown)
                    markwonWithHighlight.render(node)
                } else {
                    val node = markwonBasic.parse(markdown)
                    markwonBasic.render(node)
                }

                // 存入缓存
                memoryCache.put(cacheKey, result)

                return@withContext result

            } catch (e: Exception) {
                // 记录错误
                Log.e("MarkdownRepo", "解析Markdown失败", e)

                // 出错时，尝试以最简单的方式呈现内容
                try {
                    return@withContext markwonBasic.toMarkdown(
                        markdown.take(1000) + if (markdown.length > 1000) "...(内容过长)" else ""
                    )
                } catch (fallbackError: Exception) {
                    // 如果连基本解析都失败，则返回错误提示
                    throw IllegalArgumentException("Markdown解析失败: ${e.localizedMessage}", e)
                }
            }
        }

    /**
     * 清除缓存
     */
    override suspend fun clearCache() = withContext(Dispatchers.IO) {
        memoryCache.evictAll()
    }
}
