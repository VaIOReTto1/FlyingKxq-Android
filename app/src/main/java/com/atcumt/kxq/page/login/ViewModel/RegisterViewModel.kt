package com.atcumt.kxq.page.login.ViewModel

import android.os.Build
import com.atcumt.kxq.utils.network.auth.RegisterService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI 状态
sealed class RegisterState {
    data object Idle : RegisterState()
    data object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val error: String) : RegisterState()
}

// 用户意图
sealed class RegisterIntent {
    data class Register(val username: String, val password: String) : RegisterIntent()
}

class RegisterViewModel : ViewModel() {
    // UI 状态管理
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    // 接收用户意图
    val intentChannel = Channel<RegisterIntent>(Channel.UNLIMITED)

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
    private fun handleRegister(
        username: String, password: String, unifiedAuthToken: String = "", qqAuthorizationCode: String = "",
        appleAuthorizationCode: String = ""
    ) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            try {
                val response = RegisterService().registerBlocking(
                    RegisterService.RegisterRequest(
                        deviceType = "${Build.MANUFACTURER} ${Build.MODEL}",
                        unifiedAuthToken = unifiedAuthToken, // 这里可以根据需要生成或传递令牌
                        username = username,
                        password = password,
                        qqAuthorizationCode = qqAuthorizationCode // 如果有QQ授权代码可以传递
                    )
                )
                if (response.code == 200) {
                    _state.value = RegisterState.Success("注册成功！")
                } else {
                    _state.value = RegisterState.Error(response.msg ?: "注册失败")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error("网络异常：${e.localizedMessage}")
            }
        }
    }
}
