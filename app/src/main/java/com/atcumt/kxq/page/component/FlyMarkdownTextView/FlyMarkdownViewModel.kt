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
    init {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    repo.parse("", false)
                }
            }.onSuccess { spanned ->
                _state.value = MarkdownState.Success(
                    spanned = spanned,
                    sourceMarkdown = "",
                    visibleChars = 0
                )
                parseCache["__empty__false"] = spanned
            }.onFailure { error ->
                _state.value = MarkdownState.Error(
                    message = "初始解析失败: ${error.localizedMessage ?: "未知错误"}",
                    exception = error,
                    retryIntent = null
                )
            }
        }
    }

    fun process(intent: MarkdownIntent) {
        when (intent) {
            is MarkdownIntent.Parse -> parseMarkdown(intent)
            is MarkdownIntent.CancelParsing -> cancelCurrentParsing()
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
                var visibleChars = 0
                val fullText = intent.markdown

                if (fullText.isBlank()) {
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

                while (visibleChars < fullText.length) {
                    val charsToAdd = minOf(intent.maxConcurrentChars, fullText.length - visibleChars)
                    val nextChars = fullText.substring(visibleChars, visibleChars + charsToAdd)
                    val newlineIndex = nextChars.indexOf('\n')
                    val actualCharsToAdd = if (newlineIndex >= 0) {
                        if (newlineIndex == 0) 1 else newlineIndex
                    } else {
                        charsToAdd
                    }

                    visibleChars += actualCharsToAdd
                    val visibleText = fullText.substring(0, visibleChars)
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
                                    isCodeBlockColorful = intent.isCodeBlockColorful && visibleChars == fullText.length
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
                    val delayMs = if (visibleChars > 0 && fullText[visibleChars - 1] == '\n') {
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