package com.atcumt.kxq.page.RegisterPage

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
import com.atcumt.kxq.utils.FlyButton.FlyButton
import com.atcumt.kxq.utils.FlyButton.FlyWeakenButton
import com.atcumt.kxq.utils.FlyText.AppbarTitle
import com.atcumt.kxq.utils.FlyText.ButtonText
import com.atcumt.kxq.utils.FlyText.LabelText
import com.atcumt.kxq.utils.FlyText.WeakenButtonText
import com.atcumt.kxq.utils.FlyTextField
import wdp

@Composable
fun RegisterPage(navController: NavController) {
    Column {
        TopBar(navController)
        RegisterContent(navController)
    }
}

@Composable
fun TopBar(navController: NavController) {
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
            BackButton(navController)
        }
        AppbarTitle(
            text = "注册",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BackButton(navController: NavController) {
    Image(
        painter = painterResource(id = R.drawable.round_arrow_back_ios_new_24),
        contentDescription = "back",
        modifier = Modifier
            .padding(start = 18.wdp)
            .size(19.wdp, 48.wdp)
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) { navController.popBackStack() }
    )
}

@Composable
fun RegisterContent(navController: NavController) {
    var bindQQ = false;
    var bindSchool = false;
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterSection(
            labelText = "请先点击下方按钮认证，证明您是矿大师生",
            buttonContent = { WeakenButtonText("点我认证") },
            onClick = { navController.navigate("register") }
        )

        UserInputFields(
            listOf(
                "设置用户名（6-16位，英文数字下划线组成）" to "用户名",
                "设置密码（8-20位，无空格）" to "密码",
                "取个个性的名字吧" to "昵称"
            )
        )

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

        FlyButton(
            content = { ButtonText("注册并登录") },
            modifier = Modifier.padding(top = 69.wdp).height(45.wdp).width(329.wdp),
            onClick = {}
        )
    }
}

@Composable
fun RegisterSection(
    labelText: String,
    buttonContent: @Composable (() -> Unit),
    buttonModifier: Modifier = Modifier.padding(top = 6.wdp).height(45.wdp).width(329.wdp),
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 10.wdp),
        horizontalAlignment = Alignment.Start
    ) {
        LabelText(labelText)
        FlyWeakenButton(
            content = buttonContent,
            modifier = buttonModifier,
            width = 1.wdp,
            onClick = onClick
        )
    }
}

@Composable
fun UserInputFields(fields: List<Pair<String, String>>) {
    fields.forEach { (label, placeholder) ->
        Column(
            modifier = Modifier.padding(vertical = 10.wdp),
            horizontalAlignment = Alignment.Start
        ) {
            LabelText(label)
            FlyTextField(
                text = placeholder,
                modifier = Modifier.padding(top = 6.wdp).height(45.wdp).width(329.wdp)
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