package com.atcumt.kxq.page.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.ui.theme.PingFangFamily
import kotlinx.coroutines.delay

@Composable
fun FlyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        modifier = modifier,
        fontFamily = PingFangFamily,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypewriterFadeText(
    fullText: String,
    charDelay: Long = 50L,
    fadeInDuration: Int = 300,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    // 将文字拆成字符列表
    val chars = remember(fullText) { fullText.toList() }

    // 已经展示的字符数
    var revealCount by remember { mutableIntStateOf(0) }

    // 用来对比上一次的 fullText，判断是不是同一个消息在追加
    var prevText by remember { mutableStateOf("") }

    LaunchedEffect(fullText) {

        // 如果文本变短，或者是全新的空->非空，就认为是“新消息”，重置计数
        if (fullText.length < prevText.length || prevText.isEmpty()) {
            revealCount = 0
        }
        prevText = fullText

        // 只对新增的字符做延迟动画
        for (i in revealCount until fullText.length) {
            delay(charDelay)
            revealCount = i + 1
        }
    }

    FlowRow(modifier = modifier) {
        chars.take(revealCount).forEach { c ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(fadeInDuration))
            ) {
                Text(text = c.toString(), style = style)
            }
        }
    }
}


object FlyText {
    @Composable
    fun Text(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }

    @Composable
    fun Title(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )
    }

    @Composable
    fun AppbarTitle(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }

    @Composable
    fun SubTitle(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier
        )
    }

    @Composable
    fun TextFieldText(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }

    @Composable
    fun ButtonText(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = FlyColors.FlyBackground,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }

    @Composable
    fun WeakenButtonText(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = FlyColors.FlyMain,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }

    @Composable
    fun LabelText(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text, color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.labelLarge,
            modifier = modifier
        )
    }

    @Composable
    fun TabText(text: String, modifier: Modifier = Modifier, isSelected: Boolean) {
        Text(
            text = text,
            style = if (isSelected) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }

    @Composable
    fun NavBottomBarText(text: String, modifier: Modifier = Modifier, isSelected: Boolean) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
        )
    }

    @Composable
    fun SignalText(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.labelSmall,
            modifier = modifier
        )
    }
}