package com.atcumt.kxq.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
object FlyColors {
    val FlyMain: Color
        @Composable get() = Color(0xFF1EA59E)

    val FlyMainLight: Color
        @Composable get() = Color(0xFF28CCC3)

    val FlyDivider: Color
        @Composable get() = dynamicColor(light = Color(0xFFF7F7F8), dark = Color(0xFF1C1C1E))

    val FlySecondaryBackground: Color
        @Composable get() = dynamicColor(light = Color(0xFFF7F7F8), dark = Color(0xFF1C1C1E))

    val FlyBackground: Color
        @Composable get() = dynamicColor(light = Color(0xFFFFFFFF), dark = Color(0xFF000000))

    val FlyTextGray: Color
        @Composable get() = dynamicColor(light = Color(0xFF7F7F7F), dark = Color(0xFF97989F))

    val FlyText: Color
        @Composable get() = dynamicColor(light = Color(0xFF000000), dark = Color(0xFFFFFFFF))

    val FlyLightGray: Color
        @Composable get() = dynamicColor(light = Color(0xFFDDDDDD), dark = Color(0xFF1C1C1E))

    val FlyChipBackground: Color
        @Composable get() = dynamicColor(light = Color(0xFFEBEDF0), dark = Color(0xFF23242B))
}

@Composable
fun dynamicColor(light: Color, dark: Color): Color {
    return if (isSystemInDarkTheme()) dark else light
}