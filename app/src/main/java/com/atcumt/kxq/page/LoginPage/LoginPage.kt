package com.atcumt.kxq.page.LoginPage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.utils.FlyButton.FlyButton
import com.atcumt.kxq.utils.FlyButton.FlyWeakenButton
import com.atcumt.kxq.utils.FlyText.ButtonText
import com.atcumt.kxq.utils.FlyText.LabelText
import com.atcumt.kxq.utils.FlyText.SubTitle
import com.atcumt.kxq.utils.FlyText.Title
import com.atcumt.kxq.utils.FlyText.WeakenButtonText
import com.atcumt.kxq.utils.FlyTextField
import com.atcumt.kxq.utils.NavigationSetup
import wdp

@Composable
fun LoginPage(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 26.wdp),
        horizontalAlignment = Alignment.Start
    ) {
        // 欢迎语部分
        Column(modifier = Modifier.padding(top = 126.wdp)) {
            Title("Hi 欢迎使用矿小圈")
            Spacer(modifier = Modifier.padding(top = 4.wdp))
            SubTitle("矿大人自己的社交空间")
        }

        // 登录表单部分
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 127.wdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlyTextField(
                text = "用户名",
                modifier = Modifier.height(45.wdp).width(329.wdp)
            )
            Spacer(modifier = Modifier.padding(top = 19.wdp))
            FlyTextField(
                text = "密码",
                modifier = Modifier.height(45.wdp).width(329.wdp)
            )

            // 登录和注册按钮
            Spacer(modifier = Modifier.padding(top = 63.wdp))
            FlyButton(
                { ButtonText("登录") },
                modifier = Modifier.height(45.wdp).width(216.wdp),
                onClick = {
                    Log.d("test","111")
                }
            )
            Spacer(modifier = Modifier.padding(top = 19.wdp))
            FlyWeakenButton(
                { WeakenButtonText("注册") },
                modifier = Modifier.height(45.wdp).width(216.wdp),
                onClick = {
                    navController.navigate("register")
                }
            )

            // 隐私政策和忘记密码部分
            Spacer(
                modifier = Modifier.weight(1f).heightIn(min = 5.wdp)
            )

            // 小圆圈图标
            SmallCircleIcon(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {}
            )
            Spacer(
                modifier = Modifier.heightIn(max = 32.wdp)
                    .weight(0.36f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LabelText("隐私政策")
                Spacer(modifier = Modifier.width(25.wdp))
                LabelText("忘记密码")
            }

            Spacer(
                modifier = Modifier.heightIn(max = 36.wdp)
                    .weight(0.41f) // 最大高度限制
            )
        }
    }
}

@Composable
fun SmallCircleIcon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(30.wdp)
            .background(
                color = Color(0xFF67ADE4),
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.wdp)
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
fun Preview() {
    KxqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AdaptiveScreen {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavigationSetup()
                }
            }
        }
    }
}
