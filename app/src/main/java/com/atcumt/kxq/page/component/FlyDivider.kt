package com.atcumt.kxq.page.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp

@Composable
fun FlyDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth(),
        color = FlyColors.FlyDivider,  // 颜色设置
        thickness = 1.wdp  // 分割线厚度
    )
}