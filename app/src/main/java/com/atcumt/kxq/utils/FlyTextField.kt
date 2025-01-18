package com.atcumt.kxq.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.atcumt.kxq.utils.FlyText.TextFieldText
import wdp

@Composable
fun FlyTextField(text: String,modifier: Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(26.wdp)
            ),
        contentAlignment = Alignment.Center
    ) {
        TextFieldText(text)
    }
}