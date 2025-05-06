package com.atcumt.kxq.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.atcumt.kxq.utils.wdp

/**
 * 可复用的图标按钮，支持自定义尺寸、颜色、背景、圆角及点击事件。
 *
 * 设计要点：
 * 1. 完全无状态，所有事件通过 onClick 上抛，方便 MVVM/MVI 单向数据流。
 * 2. 主题与样式解耦：颜色、尺寸、圆角均可通过默认值或外部 FlyTheme/FlyButtonDefaults 提供。
 * 3. 无障碍支持：必须提供 contentDescription。
 * 4. Ripple 效果与 InteractionSource 可测可控。
 * 5. 可测试性：modifier + semantics 支持 UI 测试和快照测试。
 */
@Composable
fun FlyIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = FlyButtonDefaults.IconSize,
    iconTint: Color = FlyButtonDefaults.IconTint,
    backgroundColor: Color = Color.Transparent,
    cornerRadius: Dp = FlyButtonDefaults.CornerRadius,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = rememberRipple(bounded = true, radius = size / 2),
                interactionSource = remember { MutableInteractionSource() }
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier
                // 图标占用约 60% 大小，并居中
                .size(size * 0.6f)
        )
    }
}

/**
 * 默认样式常量，可在全局主题中覆盖
 */
object FlyButtonDefaults {
    /** 按钮整体尺寸 */
    val IconSize: Dp = 24.wdp
    /** 图标默认着色 */
    val IconTint: Color = MaterialTheme.colorScheme.onBackground
    /** 圆角半径 */
    val CornerRadius: Dp = 100.wdp
}
