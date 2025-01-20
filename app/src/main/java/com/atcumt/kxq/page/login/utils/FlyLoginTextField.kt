package com.atcumt.kxq.page.login.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.atcumt.kxq.page.component.FlyText.TextFieldText
import com.atcumt.kxq.utils.wdp

@Composable
fun FlyLoginTextField(text: String, modifier: Modifier, round:Dp = 26.wdp) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(round)
            ),
        contentAlignment = Alignment.Center
    ) {
        TextFieldText(text)
    }
}