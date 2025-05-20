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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atcumt.kxq.utils.ssp

@Composable
fun FlyMarkdown(
    markdown: String,
    modifier: Modifier = Modifier,
    onLinkClick: ((String) -> Boolean)? = null,
    viewModel: FlyMarkdownViewModel = hiltViewModel(),
    textColor: Color = FlyColors.FlyText,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    errorColor: Color = Color(0xFFEF9A9A),
    lineSpacingMultiplier: Float = 1.5f,
    lineSpacingExtra: Float = 0f,
    isCodeBlockColorful: Boolean = true
) {
    val state by viewModel.state.collectAsState()
    val density = LocalDensity.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is MarkdownState.Success -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(200)) + expandVertically(tween(200))
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.wdp),
                        factory = { context ->
                            TextView(context).apply {
                                setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                                setTextColor(textColor.toArgb())
                                textSize = with(density) { style.fontSize.toPx() / density.density }
                                isClickable = true
                                linksClickable = true
                            }
                        },
                        update = { textView ->
                            textView.text = currentState.spanned
                            if (onLinkClick != null) {
                                // 实现链接点击处理（需根据实际需求完善）
                                textView.setOnClickListener { view ->
                                    // 处理链接点击
                                }
                            }
                        }
                    )
                }
            }
            is MarkdownState.Error -> {
                MarkdownError(
                    errorMessage = currentState.message,
                    errorColor = errorColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

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