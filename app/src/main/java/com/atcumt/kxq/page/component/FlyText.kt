package com.atcumt.kxq.page.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.ui.theme.PingFangFamily

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