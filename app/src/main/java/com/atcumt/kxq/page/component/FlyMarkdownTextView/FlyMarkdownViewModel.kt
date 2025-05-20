package com.atcumt.kxq.page.component.FlyMarkdownTextView

import android.text.Spanned
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [功能说明] Markdown Intent - 用户操作意图
 */
sealed class MarkdownIntent {
    data class Parse(
        val markdown: String,
        val isCodeBlockColorful: Boolean = true,
        val animate: Boolean = true, // 是否启用打字机动画
        val charDelay: Long = 40L, // 每字符延迟
        val maxConcurrentChars: Int = 5 // 每批次最大字符数
    ) : MarkdownIntent()

    object CancelParsing : MarkdownIntent()
    object ClearCache : MarkdownIntent()
}

/**
 * [功能说明] Markdown 状态
 */
sealed class MarkdownState {
    data class Success(
        val spanned: Spanned?,
        val sourceMarkdown: String,
        val visibleChars: Int = Int.MAX_VALUE // 当前可见字符数
    ) : MarkdownState()

    data class Error(
        val message: String,
        val exception: Throwable? = null,
        val retryIntent: MarkdownIntent? = null
    ) : MarkdownState()
}

/**
 * [功能说明] Markdown ViewModel - 负责处理Markdown解析和打字机动画逻辑
 */
@HiltViewModel
class FlyMarkdownViewModel @Inject constructor(
    private val repo: MarkdownRepository
) : ViewModel() {
    private val latestMarkdown = MutableStateFlow("")

    private val _state = MutableStateFlow<MarkdownState>(
        MarkdownState.Success(
            spanned = null, // Initially null to avoid synchronous parse
            sourceMarkdown = "",
            visibleChars = 0
        )
    )
    val state: StateFlow<MarkdownState> = _state.asStateFlow()

    private var currentParsingJob: Job? = null
    private val parseCache = mutableMapOf<String, Spanned>()

    // Initialize empty Markdown parsing in a coroutine
    /* 单一协程，生命周期内只启动一次 */
    init {
        viewModelScope.launch {
            var visibleChars = 0
            var colorful = true
            latestMarkdown.collectLatest { fullText ->
                /* 每当有新字符 append，只更新目标长度 */
                while (visibleChars < fullText.length) {
                    val batch = minOf(1, fullText.length - visibleChars)
                    visibleChars += batch
                    val slice = fullText.substring(0, visibleChars)

                    // 快速（无高亮）解析，可用缓存
                    val spannedQuick = withContext(Dispatchers.IO) {
                        repo.parse(slice, isCodeBlockColorful = false)
                    }

                    _state.value = MarkdownState.Success(
                        spanned = spannedQuick,
                        sourceMarkdown = slice,
                        visibleChars = visibleChars
                    )
                    delay(40L)
                }

                /* 完成后做一次完整高亮解析（只做一次，避免卡顿） */
                if (colorful) {
                    val spanned = withContext(Dispatchers.IO) {
                        repo.parse(fullText, isCodeBlockColorful = true)
                    }
                    _state.value = MarkdownState.Success(
                        spanned = spanned,
                        sourceMarkdown = fullText,
                        visibleChars = fullText.length
                    )
                    colorful = false      // 已做过高亮
                }
            }
        }
    }

    fun process(intent: MarkdownIntent) {
        when (intent) {
            is MarkdownIntent.Parse -> latestMarkdown.value = intent.markdown
            is MarkdownIntent.CancelParsing -> {
//                cancelCurrentParsing()
            }
            is MarkdownIntent.ClearCache -> clearCache()
        }
    }

    private fun parseMarkdown(intent: MarkdownIntent.Parse) {
        viewModelScope.launch {
            currentParsingJob?.cancelAndJoin()

            // 检查缓存
            val fullCacheKey = "${intent.markdown}_${intent.isCodeBlockColorful}"
            parseCache[fullCacheKey]?.let { cached ->
                _state.value = MarkdownState.Success(
                    spanned = cached,
                    sourceMarkdown = intent.markdown,
                    visibleChars = if (intent.animate) 0 else intent.markdown.length
                )
                if (!intent.animate) return@launch
            }

            // 打字机动画逻辑
            currentParsingJob = viewModelScope.launch {
                val currentState = state.value as? MarkdownState.Success
                val previousFullText = currentState?.sourceMarkdown ?: ""
                val previousVisibleChars = currentState?.visibleChars ?: 0

                var visibleChars = if (intent.markdown.startsWith(previousFullText)) {
                    previousVisibleChars
                } else {
                    0
                }

                if (intent.markdown.isBlank()) {
                    val emptySpanned = parseCache["__empty__false"] ?: withContext(Dispatchers.IO) {
                        repo.parse("", false)
                    }
                    _state.value = MarkdownState.Success(
                        spanned = emptySpanned,
                        sourceMarkdown = "",
                        visibleChars = 0
                    )
                    return@launch
                }

                while (visibleChars < intent.markdown.length) {
                    val charsToAdd =
                        minOf(intent.maxConcurrentChars, intent.markdown.length - visibleChars)
                    val nextChars =
                        intent.markdown.substring(visibleChars, visibleChars + charsToAdd)
                    val newlineIndex = nextChars.indexOf('\n')
                    val actualCharsToAdd = if (newlineIndex >= 0) {
                        if (newlineIndex == 0) 1 else newlineIndex
                    } else {
                        charsToAdd
                    }

                    visibleChars += actualCharsToAdd
                    val visibleText = intent.markdown.substring(0, visibleChars)
                    val cacheKey = "${visibleText}_${intent.isCodeBlockColorful}"

                    // 检查缓存
                    val cachedSpanned = parseCache[cacheKey]
                    if (cachedSpanned != null) {
                        _state.value = MarkdownState.Success(
                            spanned = cachedSpanned,
                            sourceMarkdown = visibleText,
                            visibleChars = visibleChars
                        )
                    } else {
                        runCatching {
                            withContext(Dispatchers.IO) {
                                repo.parse(
                                    markdown = visibleText,
                                    isCodeBlockColorful = intent.isCodeBlockColorful && visibleChars == intent.markdown.length
                                )
                            }
                        }.onSuccess { spanned ->
                            parseCache[cacheKey] = spanned
                            _state.value = MarkdownState.Success(
                                spanned = spanned,
                                sourceMarkdown = visibleText,
                                visibleChars = visibleChars
                            )
                        }.onFailure { error ->
                            val errorMessage = when (error) {
                                is IllegalArgumentException -> "Markdown格式错误: ${error.message}"
                                is OutOfMemoryError -> "内存不足，无法解析过大的Markdown"
                                else -> error.localizedMessage ?: "解析失败"
                            }
                            _state.value = MarkdownState.Error(
                                message = errorMessage,
                                exception = error,
                                retryIntent = intent
                            )
                            return@launch
                        }
                    }

                    // 计算延迟
                    val delayMs =
                        if (visibleChars > 0 && intent.markdown[visibleChars - 1] == '\n') {
                            intent.charDelay * 3
                        } else {
                            intent.charDelay * actualCharsToAdd / (if (actualCharsToAdd > 1) 2 else 1)
                        }
                    delay(delayMs)
                }

                // 动画完成后触发一次完整解析
                runCatching {
                    withContext(Dispatchers.IO) {
                        repo.parse(intent.markdown, intent.isCodeBlockColorful)
                    }
                }.onSuccess { spanned ->
                    parseCache[fullCacheKey] = spanned
                    _state.value = MarkdownState.Success(
                        spanned = spanned,
                        sourceMarkdown = intent.markdown,
                        visibleChars = intent.markdown.length
                    )
                }
            }
        }
    }

    private fun cancelCurrentParsing() {
        currentParsingJob?.cancel()
        currentParsingJob = null
    }

    private fun clearCache() {
        parseCache.clear()
    }

    override fun onCleared() {
        super.onCleared()
        cancelCurrentParsing()
        clearCache()
    }
}