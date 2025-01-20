package com.atcumt.kxq.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import com.atcumt.kxq.R
import com.atcumt.kxq.utils.ssp

// 定义 PingFang SC 字体
val PingFangFamily = FontFamily(
    Font(R.font.pingfang_regular, FontWeight.Normal),
    Font(R.font.pingfang_bold, FontWeight.Bold),
    Font(R.font.pingfang_light, FontWeight.Light),
    Font(R.font.pingfang_medium, FontWeight.Medium),
    Font(R.font.pingfang_semibold, FontWeight.SemiBold),
    Font(R.font.pingfang_heavy, FontWeight.Black)
)

// 设置 Typography，使用 PingFang SC 字体
val Typography = Typography(
    titleMedium = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.ssp
    ),
    titleSmall = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.ssp
    ),
    bodyMedium = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.ssp
    ),
    labelSmall = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.ssp
    ),
    bodySmall = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.ssp
    )
)
