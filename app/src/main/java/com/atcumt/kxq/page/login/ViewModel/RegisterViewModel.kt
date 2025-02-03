package com.atcumt.kxq.page.login.ViewModel

import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atcumt.kxq.utils.ValidationUtil
import com.atcumt.kxq.utils.network.auth.RegisterService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// UI状态：表示注册页面的不同状态
sealed class RegisterState {
    object Idle : RegisterState() // 空闲状态
    object Loading : RegisterState() // 加载中状态
    data class Success(val message: String) : RegisterState() // 成功状态
    data class Error(val error: String) : RegisterState() // 错误状态
}

// 用户操作意图：表示用户触发的操作
sealed class RegisterIntent {
    data class Register(val username: String, val password: String) : RegisterIntent() // 注册意图
}


class RegisterViewModel : ViewModel() {
    // UI 状态管理
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    // 事件通道（用于一次性事件）
    private val _eventChannel = Channel<Event>()
    val eventFlow = _eventChannel.receiveAsFlow()

    // 接收用户意图
    val intentChannel = Channel<RegisterIntent>(Channel.UNLIMITED)

    var unifiedAuthToken: String? by mutableStateOf(null)// 统一认证令牌
    var qqAuthorizationCode: String? by mutableStateOf(null) // QQ 认证码（如果有）

    init {
        processIntents()
    }

    // 处理用户的意图
    private fun processIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is RegisterIntent.Register -> handleRegister(
                        intent.username,
                        intent.password
                    )
                }
            }
        }
    }

    // 处理注册逻辑
    private fun handleRegister(username: String, password: String) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            try {
                // 校验
                val validationErrorMessage = isValid(username, password)
                if (validationErrorMessage != null) {
                    Log.d("RegisterViewModel", validationErrorMessage)
                    _eventChannel.send(Event.ShowToast(validationErrorMessage))
                    return@launch
                }

                // 注册请求
                val response = RegisterService().registerBlocking(
                    RegisterService.RegisterRequest(
                        deviceType = "${Build.MANUFACTURER} ${Build.MODEL}",
                        unifiedAuthToken = unifiedAuthToken.orEmpty(),
                        username = username,
                        password = password,
                        qqAuthorizationCode = qqAuthorizationCode.orEmpty()
                    )
                )



                // 判断响应状态
                if (response.code == 200) {
                    _eventChannel.send(Event.NavigateTo("main"))
                    _eventChannel.send(Event.ShowToast("注册成功！"))
                } else {
                    Log.d("NetworkLog", "Received login request: ${response.msg}")
                    _eventChannel.send(Event.ShowToast(response.msg ?: "注册失败"))
                }
            } catch (e: Exception) {
                _eventChannel.send(Event.ShowToast("网络异常：${e.localizedMessage}"))
            }
        }
    }

    // 输入校验
    private fun isValid(username: String, password: String): String? {
        if (unifiedAuthToken.isNullOrEmpty()) {
            return "请先认证矿大身份"
        }
        if (!ValidationUtil.isValidUsername(username)) {
            return "用户名只能由英文字母、数字和下划线组成，且长度为6到16位"
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return "密码由8-20位组成，不能有空格"
        }
        return null
    }
}
