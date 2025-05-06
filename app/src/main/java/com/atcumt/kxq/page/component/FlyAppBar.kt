package com.atcumt.kxq.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.wdp

sealed class AppBarEvent {
    data object Back : AppBarEvent()
    data class Action(val id: String) : AppBarEvent()
}

interface Navigator {
    fun pop()
}

@Composable
fun FlyAppBar(
    title: String,
    showBackButton: Boolean = true,
    leadingItems: List<Pair<ImageVector, String>> = emptyList(),    // Pair<icon, id>
    actionItems: List<Pair<ImageVector, String>> = emptyList(),
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    actionContent: (@Composable RowScope.() -> Unit)? = null,
    navigator: Navigator = LocalNavigator.current,                  // CompositionLocal 注入
    onEvent: (AppBarEvent) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(FlyColors.FlyBackground)
            .padding(horizontal = 24.wdp, vertical = 12.hdp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.matchParentSize().padding(horizontal = 24.wdp)
        ) {
            if (showBackButton) {
                FlyIconButton(
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                ) {
                    onEvent(AppBarEvent.Back)
                    navigator.pop()
                }
            }

            // 自定义 leading 插槽
            leadingContent?.let { it() }

            // 普通 leading Icons
            leadingItems.forEach { (icon, id) ->
                FlyIconButton(icon, id) { onEvent(AppBarEvent.Action(id)) }
            }

            Spacer(Modifier.weight(1f))

            // 普通 action Icons
            actionItems.forEach { (icon, id) ->
                FlyIconButton(icon, id) { onEvent(AppBarEvent.Action(id)) }
            }

            // 自定义 action 插槽
            actionContent?.let { it() }
        }

        // 居中标题
        FlyText(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = FlyColors.FlyText,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// CompositionLocal 提供 Navigator
val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided")
}
