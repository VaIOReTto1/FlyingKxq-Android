package com.atcumt.kxq.page.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.wdp

/**
 * FlyImageView：异步加载网络图片，支持自定义尺寸、占位和错误占位。
 *
 * @param imageUrl 网络图片地址
 * @param widthDp   宽度，单位 dp；<=0 时不限制宽度
 * @param heightDp  高度，单位 dp；<=0 时不限制高度
 * @param modifier  额外 Modifier
 * @param placeholderContent 加载中占位 Composable
 * @param errorContent       加载失败占位 Composable
 */
@Composable
fun FlyImageView(
    imageUrl: String,
    widthDp: Int = 0,
    heightDp: Int = 0,
    modifier: Modifier = Modifier,
    placeholderContent: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                strokeWidth = 2.wdp,
                modifier = Modifier.size(24.wdp),
                color = FlyColors.FlyTextGray
            )
        }
    },
    errorContent: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            IconButton(onClick = { /* 可加重试逻辑 */ }) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                    contentDescription = "加载失败，点击重试",
                    tint = FlyColors.FlyText,
                    modifier = Modifier.size(24.wdp)
                )
            }
        }
    }
) {
    // 根据传入的 widthDp/heightDp 构造 Modifier
    val imgModifier = modifier.let {
        var m = it
        if (widthDp > 0) m = m.width(widthDp.wdp)
        if (heightDp > 0) m = m.height(heightDp.hdp)
        if (widthDp <= 0 && heightDp <= 0) m = m.fillMaxWidth()
        m
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = imgModifier,
        contentScale = ContentScale.Fit
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> placeholderContent()
            is AsyncImagePainter.State.Error -> errorContent()
            else -> SubcomposeAsyncImageContent()
        }
    }
}
