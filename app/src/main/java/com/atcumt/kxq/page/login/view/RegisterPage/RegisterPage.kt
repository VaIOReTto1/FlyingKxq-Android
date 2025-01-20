package com.atcumt.kxq.page.login.view.RegisterPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atcumt.kxq.R
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.page.component.FlyButton.FlyMainButton
import com.atcumt.kxq.page.component.FlyButton.FlyWeakenButton
import com.atcumt.kxq.page.component.FlyText.AppbarTitle
import com.atcumt.kxq.page.component.FlyText.ButtonText
import com.atcumt.kxq.page.component.FlyText.LabelText
import com.atcumt.kxq.page.component.FlyText.WeakenButtonText
import com.atcumt.kxq.page.login.utils.FlyLoginTextField
import com.atcumt.kxq.utils.wdp

@Composable
fun RegisterPage(navController: NavController) {
    Column {
        // 顶部导航栏部分
        TopBar(navController)
        // 注册内容部分
        RegisterContent(navController)
    }
}

@Composable
fun TopBar(navController: NavController) {
    // 顶部导航栏
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
        AppbarTitle(
            text = "注册", // 标题
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BackButton(navController: NavController) {
    // 返回按钮
    Image(
        painter = painterResource(id = R.drawable.round_arrow_back_ios_new_24),
        contentDescription = "back",
        modifier = Modifier
            .padding(start = 18.wdp)
            .size(19.wdp, 48.wdp)
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) { navController.popBackStack() } // 点击返回上一页
    )
}

@Composable
fun RegisterContent(navController: NavController) {
    // 注册内容部分
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 认证部分
        AuthenticationSection(navController)

        // 用户输入字段
        UserInputFields(
            listOf(
                "设置用户名（6-16位，英文数字下划线组成）" to "用户名",
                "设置密码（8-20位，无空格）" to "密码",
                "取个个性的名字吧" to "昵称"
            )
        )

        // 绑定 QQ 部分
        BindQQSection(navController)

        // 注册并登录按钮
        FlyMainButton(
            content = { ButtonText("注册并登录") },
            modifier = Modifier
                .padding(top = 69.wdp)
                .height(45.wdp)
                .width(329.wdp),
            onClick = {
                // 注册逻辑处理
            }
        )
    }
}

@Composable
private fun AuthenticationSection(navController: NavController) {
    // 认证部分
    RegisterSection(
        labelText = "请先点击下方按钮认证，证明您是矿大师生",
        buttonContent = { WeakenButtonText("点我认证") },
        onClick = { navController.navigate("register") }
    )
}

@Composable
private fun BindQQSection(navController: NavController) {
    // 绑定 QQ 部分
    var bindQQ = false // 绑定状态
    RegisterSection(
        labelText = "（可选）绑定QQ后，下次可直接用QQ登录",
        buttonContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.qq),
                    contentDescription = "qq",
                    modifier = Modifier
                        .padding(end = 7.02.wdp)
                        .height(18.wdp)
                )
                WeakenButtonText(if (bindQQ) "认证成功 ✅" else "绑定QQ")
            }
        },
        onClick = { navController.navigate("register") }
    )
}

@Composable
fun RegisterSection(
    labelText: String,
    buttonContent: @Composable (() -> Unit),
    buttonModifier: Modifier = Modifier
        .padding(top = 6.wdp)
        .height(45.wdp)
        .width(329.wdp),
    onClick: () -> Unit
) {
    // 通用注册部分
    Column(
        modifier = Modifier.padding(vertical = 10.wdp),
        horizontalAlignment = Alignment.Start
    ) {
        LabelText(labelText) // 显示标签
        FlyWeakenButton(
            content = buttonContent,
            modifier = buttonModifier,
            width = 1.wdp,
            onClick = onClick // 点击事件
        )
    }
}

@Composable
fun UserInputFields(fields: List<Pair<String, String>>) {
    // 用户输入字段部分
    fields.forEach { (label, placeholder) ->
        Column(
            modifier = Modifier.padding(vertical = 10.wdp),
            horizontalAlignment = Alignment.Start
        ) {
            LabelText(label) // 输入框标签
            FlyLoginTextField(
                text = placeholder,
                modifier = Modifier
                    .padding(top = 6.wdp)
                    .height(45.wdp)
                    .width(329.wdp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewRegisterPage() {
    KxqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AdaptiveScreen {
                val navController = rememberNavController()
                Box(modifier = Modifier.fillMaxSize()) {
                    RegisterPage(navController = navController)
                }
            }
        }
    }
}
