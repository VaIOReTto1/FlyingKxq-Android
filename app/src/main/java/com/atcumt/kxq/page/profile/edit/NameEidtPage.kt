package com.atcumt.kxq.page.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.atcumt.kxq.page.component.FlyText
import com.atcumt.kxq.page.login.view.RegisterPage.BackButton
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp

@Composable
fun NameEidtPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NameEditAppBar()
        NameEditContent()
    }
}

@Composable
fun NameEditAppBar() {
    val navController = rememberNavController()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.wdp).padding(end = 12.wdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackButton(navController) // 返回按钮
        FlyText.AppbarTitle(
            text = "修改昵称"
        )
        FlyText.AppbarTitle(
            text = "保存"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditContent() {
    Box(
        modifier = Modifier
            .padding(12.wdp)
            .height(46.wdp)
            .background(FlyColors.FlyDivider, shape = RoundedCornerShape(8.wdp)),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = "卖女孩的小火柴",
            onValueChange = {},
            modifier = Modifier.padding(12.wdp).fillMaxWidth(),
            textStyle = TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 16.ssp,
                color = FlyColors.FlyText
            ),
        )
    }
}

@Preview
@Composable
fun PreviewNameEditPage() {
    KxqTheme {
        AdaptiveScreen {
            NameEidtPage()
        }
    }
}