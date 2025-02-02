package com.atcumt.kxq.page.login.view.LoginPage

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.page.component.FlyButton.FlyMainButton
import com.atcumt.kxq.page.component.FlyButton.FlyWeakenButton
import com.atcumt.kxq.page.component.FlyText.ButtonText
import com.atcumt.kxq.page.component.FlyText.LabelText
import com.atcumt.kxq.page.component.FlyText.SubTitle
import com.atcumt.kxq.page.component.FlyText.Title
import com.atcumt.kxq.page.component.FlyText.WeakenButtonText
import com.atcumt.kxq.page.login.ViewModel.LoginEvent
import com.atcumt.kxq.page.login.ViewModel.LoginIntent
import com.atcumt.kxq.page.login.ViewModel.LoginViewModel
import com.atcumt.kxq.page.login.utils.FlyLoginTextField
import com.atcumt.kxq.utils.wdp

@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // 监听一次性事件（用于显示Toast和导航）
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is LoginEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    Log.d("internet", event.message)
                }

                is LoginEvent.NavigateTo -> {
                    navController.navigate(event.route) {
                        // 清除栈中的所有页面，只有当前页面留在栈中
                        if (event.route == "main")
                            popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 26.wdp),
        horizontalAlignment = Alignment.Start
    ) {
        // 欢迎语部分
        WelcomeSection()

        // 登录表单部分
        LoginForm(viewModel)

        // 隐私政策与小圆圈部分
        PrivacyAndIconSection()
    }
}

@Composable
private fun WelcomeSection() {
    // 欢迎语部分
    Column(modifier = Modifier.padding(top = 126.wdp)) {
        Title("Hi 欢迎使用矿小圈") // 主标题
        Spacer(modifier = Modifier.padding(top = 4.wdp))
        SubTitle("矿大人自己的社交空间") // 副标题
    }
}

@Composable
fun LoginForm(
    viewModel: LoginViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 登录表单部分
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 127.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 用户名输入框
        FlyLoginTextField(
            value = username, // 绑定到状态
            onValueChange = { username = it }, // 更新状态
            modifier = Modifier
                .height(45.wdp)
                .width(329.wdp),
            placeText = "用户名"
        )
        Spacer(modifier = Modifier.padding(top = 19.wdp))

        // 密码输入框
        FlyLoginTextField(
            value = password, // 绑定到状态
            onValueChange = { password = it }, // 更新状态
            modifier = Modifier
                .height(45.wdp)
                .width(329.wdp),
            placeText = "密码"
        )

        // 登录和注册按钮
        Spacer(modifier = Modifier.padding(top = 63.wdp))
        FlyMainButton(
            { ButtonText("登录") }, // 登录按钮文本
            modifier = Modifier
                .height(45.wdp)
                .width(216.wdp),
            onClick = {
                viewModel.intentChannel.trySend(
                    LoginIntent.Login(
                        username,
                        password
                    )
                )
            }
        )
        Spacer(modifier = Modifier.padding(top = 19.wdp))
        FlyWeakenButton(
            { WeakenButtonText("注册") }, // 注册按钮文本
            modifier = Modifier
                .height(45.wdp)
                .width(216.wdp),
            onClick = {
                viewModel.intentChannel.trySend(LoginIntent.NavigateToRegister)
            }
        )
    }
}


@Composable
private fun PrivacyAndIconSection() {
    // 隐私政策与小圆圈部分
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 5.wdp)
        )

        // 小圆圈图标
        SmallCircleIcon(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                // 处理小圆圈点击事件
            }
        )
        Spacer(
            modifier = Modifier
                .heightIn(max = 32.wdp)
                .weight(0.36f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            LabelText("隐私政策") // 隐私政策链接
            Spacer(modifier = Modifier.width(25.wdp))
            LabelText("忘记密码") // 忘记密码链接
        }

        Spacer(
            modifier = Modifier
                .heightIn(max = 36.wdp)
                .weight(0.41f) // 最大高度限制
        )
    }
}

@Composable
fun SmallCircleIcon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    // 小圆圈图标组件
    Box(
        modifier = modifier
            .size(30.wdp) // 设置大小
            .background(
                color = Color(0xFF67ADE4), // 背景颜色
                shape = RoundedCornerShape(50) // 圆形形状
            )
            .clickable(onClick = onClick), // 点击事件
        contentAlignment = Alignment.Center // 内容居中
    ) {
        Icon(
            imageVector = Icons.Default.Notifications, // 通知图标
            contentDescription = null,
            tint = Color.White, // 图标颜色
            modifier = Modifier.size(18.wdp) // 图标大小
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
                    LoginPage(rememberNavController()) // 调用登录页面
                }
            }
        }
    }
}

