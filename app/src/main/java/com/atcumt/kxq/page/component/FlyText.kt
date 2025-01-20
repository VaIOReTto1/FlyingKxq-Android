package com.atcumt.kxq.page.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atcumt.kxq.ui.theme.FlyColors

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
            text = text, color = FlyColors.FlyTextDark,
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