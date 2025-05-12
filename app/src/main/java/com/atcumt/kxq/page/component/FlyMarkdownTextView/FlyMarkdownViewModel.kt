package com.atcumt.kxq.page.component.FlyMarkdownTextView

import android.text.Spanned
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import javax.inject.Inject

/**
 * [功能说明] Markdown Intent - 用户操作意图
 */
sealed class MarkdownIntent {
    /**
     * 解析Markdown文本
     * @param markdown 要解析的Markdown文本
     * @param isCodeBlockColorful 是否启用代码块语法高亮
     */
    data class Parse(
        val markdown: String, 
        val isCodeBlockColorful: Boolean = true
    ): MarkdownIntent()
    
    /**
     * 取消当前解析任务
     */
    object CancelParsing : MarkdownIntent()
    
    /**
     * 清除缓存
     */
    object ClearCache : MarkdownIntent()
}

/**
 * [功能说明] Markdown 状态
 */
sealed class MarkdownState {
    /**
     * 加载中状态
     */
    object Loading: MarkdownState()
    
    /**
     * 成功状态 - 带有渲染好的Spanned对象
     * @param spanned 已渲染的富文本
     * @param sourceMarkdown 原始Markdown文本
     */
    data class Success(
        val spanned: Spanned,
        val sourceMarkdown: String
    ): MarkdownState()
    
    /**
     * 错误状态
     * @param message 错误信息
     * @param exception 异常对象(可选)
     * @param retryIntent 重试操作的Intent(可选)
     */
    data class Error(
        val message: String,
        val exception: Throwable? = null,
        val retryIntent: MarkdownIntent? = null
    ): MarkdownState()
}

/**
 * [功能说明] Markdown ViewModel - 负责处理Markdown解析逻辑
 */
@HiltViewModel
class FlyMarkdownViewModel @Inject constructor(
    private val repo: MarkdownRepository
): ViewModel() {

    /** 当前状态 */
    private val _state = MutableStateFlow<MarkdownState>(MarkdownState.Loading)
    
    /** 公开的不可变状态流 */
    val state: StateFlow<MarkdownState> = _state.asStateFlow()
    
    /** 当前解析任务 */
    private var currentParsingJob: Job? = null
    
    /** 缓存 - 避免重复解析相同内容 */
    private val parseCache = mutableMapOf<String, Spanned>()
    
    /**
     * 处理用户操作意图
     * @param intent 用户操作意图
     */
    fun process(intent: MarkdownIntent) {
        when (intent) {
            is MarkdownIntent.Parse -> parseMarkdown(intent)
            is MarkdownIntent.CancelParsing -> cancelCurrentParsing()
            is MarkdownIntent.ClearCache -> clearCache()
        }
    }
    
    /**
     * 解析Markdown文本
     * @param intent Parse意图
     */
    private fun parseMarkdown(intent: MarkdownIntent.Parse) {
        // 先取消当前任务
        viewModelScope.launch {
            currentParsingJob?.cancelAndJoin()
            
            // 检查缓存
            val cacheKey = "${intent.markdown}_${intent.isCodeBlockColorful}"
            parseCache[cacheKey]?.let { cached ->
                _state.value = MarkdownState.Success(cached, intent.markdown)
                return@launch
            }
            
            // 设置加载状态
            _state.value = MarkdownState.Loading
            
            // 启动新任务
            currentParsingJob = viewModelScope.launch {
                runCatching {
                    repo.parse(
                        markdown = intent.markdown, 
                        isCodeBlockColorful = intent.isCodeBlockColorful
                    )
                }.onSuccess { spanned ->
                    // 成功解析，更新缓存和状态
                    parseCache[cacheKey] = spanned
                    _state.value = MarkdownState.Success(spanned, intent.markdown)
                }.onFailure { error ->
                    // 区分处理不同类型的错误
                    val errorMessage = when(error) {
                        is IllegalArgumentException -> "Markdown格式错误: ${error.message}"
                        is OutOfMemoryError -> "内存不足，无法解析过大的Markdown"
                        else -> error.localizedMessage ?: "解析失败"
                    }
                    
                    // 错误状态，包含重试选项
                    _state.value = MarkdownState.Error(
                        message = errorMessage,
                        exception = error,
                        retryIntent = intent
                    )
                }
            }
        }
    }
    
    /**
     * 取消当前解析任务
     */
    private fun cancelCurrentParsing() {
        currentParsingJob?.cancel()
        currentParsingJob = null
    }
    
    /**
     * 清除缓存
     */
    private fun clearCache() {
        parseCache.clear()
    }
    
    /**
     * ViewModel销毁时清理资源
     */
    override fun onCleared() {
        super.onCleared()
        cancelCurrentParsing()
        clearCache()
    }
}
