package com.atcumt.kxq.page.login.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaults
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaultsKey
import com.atcumt.kxq.utils.network.TokenProvider
import com.atcumt.kxq.utils.network.auth.login.LoginService
import com.atcumt.kxq.utils.network.user.info.me.UserInfoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 表示UI的不同状态
sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val error: String) : LoginState()
}

// 一次性事件密封类（新增）
sealed class Event {
    /** 显示Toast通知 */
    data class ShowToast(val message: String) : Event()

    /** 导航到指定路由 */
    data class NavigateTo(val route: String) : Event()
}

// 表示用户交互意图
sealed class LoginIntent {
    data class Login(val username: String, val password: String) : LoginIntent()
    data object NavigateToRegister : LoginIntent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val userDefaults: FlyUserDefaults,
    private val userInfoService: UserInfoService
) : ViewModel() {
    // 用于发送UI状态
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    // 输入框状态
    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    val username: StateFlow<String> = _username
    val password: StateFlow<String> = _password

    // 事件通道（用于一次性事件）
    private val _eventChannel = Channel<Event>()
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

    private fun handleLogin(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                // 1. 登录请求
                val loginResponse = withContext(Dispatchers.IO) {
                    LoginService().loginBlocking(
                        LoginService.LoginRequest(
                            deviceType = "${Build.MANUFACTURER} ${Build.MODEL}",
                            username = username,
                            password = password
                        )
                    )
                }

                if (loginResponse.code == 200 && loginResponse.data != null) {
                    // 2. 异步获取用户信息
                    launch(Dispatchers.IO) {
                        try {
                            val data = loginResponse.data

                            // —— 💾 存储 token ——
                            tokenProvider.saveToken(
                                data.accessToken.orEmpty(),
                                data.refreshToken.orEmpty()
                            )

                            // —— 计算并保存到期时间戳（毫秒）——
                            val expiresInSec = data.expiresIn ?: 0L
                            val expiresAt = System.currentTimeMillis() + expiresInSec * 1000L
                            userDefaults.set(expiresAt, FlyUserDefaultsKey.TOKEN_EXPIRES_AT)

                            // —— 标记已登录 ——
                            userDefaults.set(true, FlyUserDefaultsKey.IS_LOGGED_IN)
                            userDefaults.set(data.userId, FlyUserDefaultsKey.USER_ID)

                            userInfoService.getUserInfoBlocking()
                            withContext(Dispatchers.Main) {
                                // 3. 更新UI状态
                                _eventChannel.send(Event.NavigateTo("main"))
                                _eventChannel.send(Event.ShowToast("登录成功！"))
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                _eventChannel.send(Event.ShowToast("用户信息获取失败: ${e.message}"))
                            }
                        }
                    }
                } else {
                    _eventChannel.send(Event.ShowToast(loginResponse.msg ?: "登录失败"))
                }
            } catch (e: Exception) {
                _eventChannel.send(Event.ShowToast("网络异常：${e.localizedMessage}"))
            } finally {
                _state.value = LoginState.Idle
            }
        }
    }

    private fun navigateToRegister() {
        viewModelScope.launch {
            _eventChannel.send(Event.NavigateTo("register"))
        }
    }
}