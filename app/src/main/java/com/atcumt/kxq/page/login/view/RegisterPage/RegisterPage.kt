package com.atcumt.kxq.page.login.view.RegisterPage

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
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
import com.atcumt.kxq.page.login.viewmodel.Event
import com.atcumt.kxq.page.login.viewmodel.RegisterIntent
import com.atcumt.kxq.page.login.viewmodel.RegisterViewModel
import com.atcumt.kxq.page.login.utils.FlyLoginTextField
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp

@Composable
fun RegisterPage(
    navController: NavController,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    // 监听一次性事件（用于显示Toast和导航）
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is Event.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG)
                        .show() // 显示注册过程中的Toast
                }

                is Event.NavigateTo -> {
                    navController.navigate(event.route) {
                        // 清除栈中的所有页面，只有当前页面留在栈中
                        if (event.route == "main")
                            popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }
    Column {
        // 顶部导航栏部分
        TopBar(navController)
        // 注册内容部分
        RegisterContent(navController, viewModel)
    }
}

@Composable
fun TopBar(navController: NavController) {
    // 顶部导航栏
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.wdp)
            .background(FlyColors.FlyBackground)
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
                interactionSource = remember { MutableInteractionSource() }
            ) { navController.popBackStack() }, // 点击返回上一页
        colorFilter = ColorFilter.tint(FlyColors.FlyText)
    )
}

@Composable
fun RegisterContent(navController: NavController, viewModel: RegisterViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 认证部分
        AuthenticationSection(navController, viewModel)

        // 用户输入字段
        UserInputFields(
            fields = listOf(
                "设置用户名（6-16位，英文数字下划线组成）" to "用户名",
                "设置密码（8-20位，无空格）" to "密码"
            ),
            username = username,
            password = password,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it }
        )

        BindQQSection(navController)

        // 注册按钮
        FlyMainButton(
            content = { ButtonText("注册并登录") },
            modifier = Modifier
                .padding(top = 69.wdp)
                .height(45.wdp)
                .width(329.wdp),
            onClick = {
                viewModel.intentChannel.trySend(RegisterIntent.Register(username, password))
            }
        )
    }
}


@Composable
private fun AuthenticationSection(navController: NavController, viewModel: RegisterViewModel) {
    // 获取保存的 unifiedAuthToken
    val tokenState = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("unifiedAuthToken")
        ?.observeAsState()

    // 获取 token 的值
    val token = tokenState?.value
    token?.let {
        if (viewModel.unifiedAuthToken != token) {  // 确保 token 更新时才赋值
            viewModel.unifiedAuthToken = it
        }
    }
    RegisterSection(
        labelText = "请先点击下方按钮认证，证明您是矿大师生",
        buttonContent = { WeakenButtonText(if (viewModel.unifiedAuthToken != null) "认证成功 ✅" else "点我认证") },
        onClick = { navController.navigate("school") }
    )
}


@Composable
private fun BindQQSection(navController: NavController) {
    // 绑定 QQ 部分
    val bindQQ = false // 绑定状态
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
fun UserInputFields(
    fields: List<Pair<String, String>>,
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    fields.forEach { (label, placeholder) ->
        Column(
            modifier = Modifier.padding(vertical = 10.wdp),
            horizontalAlignment = Alignment.Start
        ) {
            LabelText(label) // 输入框标签
            when (placeholder) {
                "用户名" -> FlyLoginTextField(
                    value = username,
                    onValueChange = onUsernameChange, // 绑定到 username
                    placeText = placeholder,
                    modifier = Modifier
                        .padding(top = 6.wdp)
                        .height(45.wdp)
                        .width(329.wdp)
                )

                "密码" -> FlyLoginTextField(
                    value = password,
                    onValueChange = onPasswordChange, // 绑定到 password
                    placeText = placeholder,
                    modifier = Modifier
                        .padding(top = 6.wdp)
                        .height(45.wdp)
                        .width(329.wdp)
                )

                else -> FlyLoginTextField(
                    value = "", // 对于昵称字段我们暂时设置空值
                    onValueChange = { },
                    placeText = placeholder,
                    modifier = Modifier
                        .padding(top = 6.wdp)
                        .height(45.wdp)
                        .width(329.wdp)
                )
            }
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