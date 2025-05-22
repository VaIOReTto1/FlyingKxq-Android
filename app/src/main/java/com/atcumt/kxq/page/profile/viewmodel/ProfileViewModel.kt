// ProfileViewModel.kt
package com.atcumt.kxq.page.profile.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atcumt.kxq.page.profile.dao.UserRepository
import com.atcumt.kxq.page.profile.dao.UserWithStatuses
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaults
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaultsKey
import com.atcumt.kxq.utils.network.user.info.me.UserInfoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI State：加载中 / 成功 / 错误
sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val data: UserWithStatuses?) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: UserRepository,
    private val service: UserInfoService,
    userDefaults: FlyUserDefaults    // ← 注入你自己的本地存储
) : ViewModel() {

    private val uid: String = userDefaults.get(FlyUserDefaultsKey.USER_ID)
        ?: throw IllegalStateException("当前没有已登录用户")

    // 内部可变状态
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)

    // 对外不可变
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // 先订阅本地缓存，再触发网络刷新
        observeLocal()
        refresh()
    }

    fun dumpLocalDatabase() {
        viewModelScope.launch {
            try {
                val users = repo.fetchAllUsers()
                val statuses = repo.fetchAllStatuses()
                Log.d("ProfileViewModel", "📦 本地 users = $users")
                Log.d("ProfileViewModel", "📦 本地 statuses = $statuses")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "❌ 打印本地数据失败", e)
            }
        }
    }

    /** 订阅本地数据库变化，任何更新都会反映到 UI */
    private fun observeLocal() {
        viewModelScope.launch {
            repo.observeMe(uid)
                .onStart {
                    dumpLocalDatabase()
                    Log.d("ProfileViewModel", "👀 开始订阅本地数据(uid=$uid)")
                }
//                .filter { it != null }
                .onEach { userWithStatuses ->
                    Log.d("ProfileViewModel", "← 本地数据 emit: $userWithStatuses")
                    _uiState.update { prev ->
                        when (prev) {
                            is ProfileUiState.Loading,
                            is ProfileUiState.Error -> ProfileUiState.Success(userWithStatuses)

                            is ProfileUiState.Success -> prev.copy(data = userWithStatuses)
                        }
                    }
                }
                .catch { e ->
                    Log.e("ProfileViewModel", "⚠️ observeLocal 出错", e)
                    _uiState.value = ProfileUiState.Error("读取本地数据失败：${e.localizedMessage}")
                }
                .collect()
        }
    }

    /** 从网络刷新，成功后本地缓存更新会自动驱动 UI */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val resp = service.getUserInfoBlocking()
                Log.d("ProfileViewModel", "← refresh response: $resp")
                // 缓存写入后，本地 observeLocal 会触发 Success
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "⚠️ refresh 失败", e)
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "网络请求失败")
            }
        }
    }
}
