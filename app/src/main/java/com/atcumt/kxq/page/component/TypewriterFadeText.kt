package com.atcumt.kxq.page.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay

/**
 * [功能说明] 逐字淡入的打字机效果文本组件
 * 
 * 支持逐字显示文本，每个字符以淡入动画出现，模拟打字机效果
 * 
 * @param fullText 完整要显示的文本
 * @param animate 是否启用动画（false则直接显示全部）
 * @param charDelay 每个字符显示的延迟（毫秒）
 * @param fadeInDuration 字符淡入动画时长（毫秒）
 * @param modifier 布局修饰符
 * @param style 文本样式
 * @param maxConcurrentChars 每批次添加的最大字符数（性能优化）
 * @param preserveNewlines 是否保留换行符位置（针对\n字符）
 */
@Composable
fun TypewriterFadeText(
    fullText: String,
    animate: Boolean = true,
    charDelay: Long = 40L,
    fadeInDuration: Int = 200,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
    maxConcurrentChars: Int = 1,
    preserveNewlines: Boolean = false,
    onComplete: (() -> Unit)? = null
) {
    if (fullText.isBlank()) {
        return
    }

    // 使用fullText作为key，确保fullText变化时重置字符数
    var visibleChars by remember{ mutableIntStateOf(if (animate) 0 else fullText.length) }
    
    // 动画逻辑
    LaunchedEffect(fullText, animate) {
        if (!animate || visibleChars >= fullText.length) {
            visibleChars = fullText.length
            return@LaunchedEffect
        }
        
        // 逐字增加显示
        while (visibleChars < fullText.length) {
            // 计算要添加的字符数，考虑换行符的特殊处理
            var charsToAdd = minOf(maxConcurrentChars, fullText.length - visibleChars)
            
            // 如果preserveNewlines=true，处理换行符位置
            if (preserveNewlines && charsToAdd > 1) {
                // 检查即将添加的字符块中是否有换行符
                val nextChars = fullText.substring(visibleChars, visibleChars + charsToAdd)
                val newlineIndex = nextChars.indexOf('\n')
                
                // 如果找到换行符，调整添加的字符数，确保换行符单独添加
                if (newlineIndex >= 0) {
                    charsToAdd = if (newlineIndex == 0) {
                        // 如果换行符在第一个位置，只添加它
                        1
                    } else {
                        // 添加到换行符为止
                        newlineIndex 
                    }
                }
            }
            
            // 增加可见字符
            visibleChars += charsToAdd
            
            // 计算延迟：换行符出现后额外延迟
            val nextDelay = if (preserveNewlines && 
                              visibleChars > 0 && 
                              visibleChars <= fullText.length && 
                              fullText[visibleChars - 1] == '\n') {
                charDelay * 3  // 换行符后延迟更长
            } else {
                charDelay * charsToAdd / (if (charsToAdd > 1) 2 else 1) // 批处理加速
            }
            
            delay(nextDelay)
        }
        
        // 确保最终显示完整文本
        visibleChars = fullText.length
        onComplete?.invoke()
    }
    
    // 显示当前可见的部分文本
    Box(modifier = modifier) {
        val visibleText = if (visibleChars >= fullText.length) {
            fullText
        } else {
            fullText.substring(0, visibleChars)
        }

        if (animate && visibleChars < fullText.length) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(fadeInDuration))
            ) {
                Text(
                    text = visibleText,
                    style = style,
                    textAlign = textAlign
                )
            }
        } else {
            Text(
                text = visibleText,
                style = style,
                textAlign = textAlign
            )
        }
    }
} 