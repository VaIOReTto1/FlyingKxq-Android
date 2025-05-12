package com.atcumt.kxq.page.component.FlyMarkdownTextView

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atcumt.kxq.utils.ssp

/**
 * [功能说明] Markdown 渲染组件
 * @param markdown Markdown 文本
 * @param modifier 布局修饰符
 * @param onLinkClick 链接点击回调 (返回true表示已处理，false表示交由系统处理)
 * @param viewModel MarkdownViewModel 实例，通常由 hilt 注入
 * @param textColor 文本颜色，默认为主题文本颜色
 * @param errorColor 错误颜色，默认为浅红色
 * @param lineSpacingMultiplier 行间距倍数，默认为1.5
 * @param lineSpacingExtra 行间距额外增加值，默认为0dp
 * @param isCodeBlockColorful 代码块是否启用语法高亮，默认为true
 */
@Composable
fun FlyMarkdown(
    markdown: String,
    modifier: Modifier = Modifier,
    onLinkClick: ((String) -> Boolean)? = null,
    viewModel: FlyMarkdownViewModel = hiltViewModel(),
    textColor: Color = FlyColors.FlyText,
    errorColor: Color = Color(0xFFEF9A9A),
    lineSpacingMultiplier: Float = 1.5f,
    lineSpacingExtra: Float = 0f,
    isCodeBlockColorful: Boolean = true
) {
    val state by viewModel.state.collectAsState()
    val density = LocalDensity.current

    LaunchedEffect(markdown, isCodeBlockColorful) {
        viewModel.process(MarkdownIntent.Parse(
            markdown = markdown, 
            isCodeBlockColorful = isCodeBlockColorful
        ))
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is MarkdownState.Loading -> {
                // 骨架屏加载状态
                MarkdownSkeleton(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is MarkdownState.Success -> {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.wdp),
                    factory = { context ->
                        TextView(context).apply {
                            setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                            setTextColor(textColor.toArgb())
//                            setLinkTextColor(FlyColors.FlyMain.toArgb())
                            // 使链接可点击，但不显示下划线
                            isClickable = true 
                            linksClickable = true
                        }
                    },
                    update = { textView ->
                        val successState = (state as? MarkdownState.Success) ?: return@AndroidView
                        
                        // 设置Markdown内容
                        textView.text = successState.spanned
                        
                        // 设置链接点击处理
                        if (onLinkClick != null) {
                            // 提取文本中的所有链接并设置点击处理器
                            textView.setOnClickListener { view ->
                                // 处理链接点击 (完整实现需处理精确点击位置)
                                // 简化示例: 链接处理放在Repository中更合适
                                // 这里仅作示意
                            }
                        }
                    }
                )
            }
            is MarkdownState.Error -> {
                // 错误状态显示
                MarkdownError(
                    errorMessage = (state as MarkdownState.Error).message,
                    errorColor = errorColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * [功能说明] Markdown 骨架屏
 * @param modifier 布局修饰符
 */
@Composable
fun MarkdownSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        // 标题骨架
        Box(
            Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(FlyColors.FlyLightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // 段落骨架
        repeat(3) { 
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(FlyColors.FlyLightGray.copy(alpha = 0.7f))
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // 短段骨架
        Box(
            Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(FlyColors.FlyLightGray.copy(alpha = 0.5f))
        )
    }
}

/**
 * [功能说明] Markdown 错误显示
 * @param errorMessage 错误信息
 * @param errorColor 错误颜色
 * @param modifier 布局修饰符
 */
@Composable
fun MarkdownError(
    errorMessage: String,
    errorColor: Color,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(300)) + expandVertically(tween(300))
    ) {
        Row(
            modifier = modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(errorColor.copy(alpha = 0.12f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "错误",
                tint = errorColor
            )
            
            Text(
                text = "Markdown解析错误: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.ssp,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
