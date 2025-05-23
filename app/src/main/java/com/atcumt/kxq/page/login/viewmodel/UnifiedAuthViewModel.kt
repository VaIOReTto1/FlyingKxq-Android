package com.atcumt.kxq.page.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.atcumt.kxq.utils.network.auth.authentication.UnifiedAuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


// 统一认证状态
sealed class UnifiedAuthState {
    data object Idle : UnifiedAuthState() // 空闲状态
    data object Loading : UnifiedAuthState() // 加载中状态
    data class Success(val message: String) : UnifiedAuthState() // 成功状态
    data class Error(val error: String) : UnifiedAuthState() // 错误状态
}

class UnifiedAuthViewModel : ViewModel() {
    // 统一认证状态管理
    private val _unifiedAuthState = MutableStateFlow<UnifiedAuthState>(UnifiedAuthState.Idle)
    val unifiedAuthState: StateFlow<UnifiedAuthState> = _unifiedAuthState

    // 存储认证令牌
    var unifiedAuthToken: String? by mutableStateOf(null)// 统一认证令牌

    private val unifiedAuthService = UnifiedAuthService()

    // 处理认证逻辑
    suspend fun authenticateWithUnifiedAuth(cookie: String) {
        _unifiedAuthState.value = UnifiedAuthState.Loading

        val unifiedAuthRequest = UnifiedAuthService.UnifiedAuthRequest(cookie)

        unifiedAuthService.unifiedAuth(unifiedAuthRequest) { response, error ->
            if (error != null) {
                _unifiedAuthState.value = UnifiedAuthState.Error("认证失败：${error.message}")
            } else {
                if (response?.code == 200) {
                    unifiedAuthToken = response.data?.token.toString() // 保存令牌\
                    _unifiedAuthState.value = UnifiedAuthState.Success("认证成功！")
                } else {
                    _unifiedAuthState.value = UnifiedAuthState.Error(response?.msg ?: "认证失败")
                }
            }
        }
        delay(1000)
    }

    // 统一认证绑定（通过 Cookie 进行认证）
    fun cookieHandler(url: String?, cookies: List<String>): Boolean {
        if (url?.contains("portal.cumt.edu.cn") == true) {
            val cookie = cookies.firstOrNull { it.contains("SESS9595") }
            if (cookie != null) {
                return true
            }
        }
        return false
    }
}