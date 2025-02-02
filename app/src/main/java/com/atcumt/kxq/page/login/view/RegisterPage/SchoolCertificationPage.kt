package com.atcumt.kxq.page.login.view.RegisterPage

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.atcumt.kxq.page.component.FlyText
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.FlyWebView
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp

@Composable
fun SchoolCertificationPage(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        SchoolCertificationAppBar(navController)
        FlyWebView("https://authserver.cumt.edu.cn/authserver/login")
    }
}

@Composable
fun SchoolCertificationAppBar(navController: NavController) {
    // 顶部导航栏
    Column {
        // 标题栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.wdp)
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(navController) // 返回按钮
            }
            FlyText.AppbarTitle(
                text = "注册",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 副标题
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.wdp)
                .background(Color.White)
        ) {
            FlyText(
                text = "在这个页面登录即可认证成功",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 14.ssp,
                fontWeight = FontWeight.W400,
                color = FlyColors.FlyTextGray
            )
        }
    }
}
