package com.atcumt.kxq.page.login.ViewModel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.atcumt.kxq.utils.network.auth.login.LoginService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 表示UI的不同状态
sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val error: String) : LoginState()
}

// 一次性事件密封类（新增）
sealed class LoginEvent {
    /** 显示Toast通知 */
    data class ShowToast(val message: String) : LoginEvent()

    /** 导航到指定路由 */
    data class NavigateTo(val route: String) : LoginEvent()
}

// 表示用户交互意图
sealed class LoginIntent {
    data class Login(val username: String, val password: String) : LoginIntent()
    data object NavigateToRegister : LoginIntent()
}

class LoginViewModel : ViewModel() {
    // 用于发送UI状态
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    // 输入框状态
    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    val username: StateFlow<String> = _username
    val password: StateFlow<String> = _password

    // 事件通道（用于一次性事件）
    private val _eventChannel = Channel<LoginEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    // 接收用户意图
    val intentChannel = Channel<LoginIntent>(Channel.UNLIMITED)

    init {
        processIntents()
    }

    private fun processIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is LoginIntent.Login -> handleLogin(intent.username, intent.password)
                    is LoginIntent.NavigateToRegister -> navigateToRegister()
                }
            }
        }
    }

    /** 更新用户名 */
    fun updateUsername(username: String) {
        _username.value = username
    }

    /** 更新密码 */
    fun updatePassword(password: String) {
        _password.value = password
    }

    private fun handleLogin(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val response = LoginService().loginBlocking(
                    LoginService.LoginRequest(
                        deviceType = "${Build.MANUFACTURER} ${Build.MODEL}",
                        username = username,
                        password = password
                    )
                )
                if (response.code == 200) {
                    _eventChannel.send(LoginEvent.NavigateTo("main"))
                    _eventChannel.send(LoginEvent.ShowToast("登录成功！"))
                } else {
                    _eventChannel.send(LoginEvent.ShowToast(response.msg ?: "登录失败"))
                }
            } catch (e: Exception) {
                _eventChannel.send(LoginEvent.ShowToast("网络异常：${e.localizedMessage}"))
            }
        }
    }

    private fun navigateToRegister() {
        viewModelScope.launch {
            _eventChannel.send(LoginEvent.NavigateTo("register"))
        }
    }
}