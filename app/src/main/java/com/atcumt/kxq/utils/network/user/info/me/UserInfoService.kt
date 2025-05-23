package com.atcumt.kxq.utils.network.user.info.me

import android.util.Log
import com.atcumt.kxq.page.profile.dao.StatusEntity
import com.atcumt.kxq.page.profile.dao.UserDao
import com.atcumt.kxq.page.profile.dao.UserEntity
import com.atcumt.kxq.page.profile.dao.UserRepository
import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_USER
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class UserInfoService @Inject constructor(
    private val repo: UserRepository      // 通过 Hilt 注入
) {
    // region 数据结构
    data class UserInfoResponse(
        @SerializedName("code") val code: Int,
        @SerializedName("msg") val msg: String,
        @SerializedName("data") val data: UserInfoData?
    ) {
        val isSuccess: Boolean get() = code == 200
    }

    data class UserInfoData(
        @SerializedName("userId") val userId: String,
        @SerializedName("username") val username: String,
        @SerializedName("nickname") val nickname: String?,
        @SerializedName("avatar") val avatar: String?,
        @SerializedName("bio") val bio: String?,
        @SerializedName("gender") val gender: Int?,
        @SerializedName("hometown") val hometown: String?,
        @SerializedName("major") val major: String?,
        @SerializedName("grade") val grade: Int?,
        @SerializedName("statuses") val statuses: List<StatusData>?,
        @SerializedName("level") val level: Int?,
        @SerializedName("experience") val experience: Int?,
        @SerializedName("followersCount") val followersCount: Int?,
        @SerializedName("followingsCount") val followingsCount: Int?,
        @SerializedName("likeReceivedCount") val likeReceivedCount: Int?
    )

    data class StatusData(
        @SerializedName("emoji") val emoji: String?,
        @SerializedName("text") val text: String?,
        @SerializedName("endTime") val endTime: String?
    )
    // endregion

    // region 本地数据
    private val localResponse = """
    {
      "code": 200,
      "msg": "成功",
      "data": {
        "userId": "e42600ed96884b989c9f9b97d992a9e9",
        "username": "qqqqqq",
        "nickname": "圈圈FUknaN",
        "avatar": "http://119.45.93.228:8080/api/file/v1/public/e59b8e263ee1764656e34ef38322f234a67ae9417d66c70652f90311db9271b3.jpg",
        "bio": "Android 爱好者，热衷于 Jetpack Compose 与 Kotlin",
        "gender": 1,
        "hometown": "北京市 海淀区",
        "major": "计算机科学与技术",
        "grade": 2023,
        "statuses": [
          {
            "emoji": "🚀",
            "text": "正在开发下一代聊天应用",
            "endTime": "2025-06-30T23:59:59Z"
          },
          {
            "emoji": "🎓",
            "text": "刚刚获得硕士学位",
            "endTime": "2024-07-01T00:00:00Z"
          }
        ],
        "level": 5,
        "experience": 1200,
        "followersCount": 256,
        "followingsCount": 128,
        "likeReceivedCount": 512
      }
    }
    """.trimIndent()
    // endregion

    // region 网络请求
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getUserInfoBlocking(): UserInfoResponse? = withContext(Dispatchers.IO) {
        // 1. 先拿到原始响应（或本地 mock）
        val parsed: UserInfoResponse? = suspendCancellableCoroutine { cont ->
            ApiServiceS.get(
                baseUrl = BASE_URL_USER,
                params = mapOf(),
                endpoint = "info/v1/me",
                headers = mapOf("Accept" to "application/json")
            ) { response, error ->
                // 把 handleResponse 的逻辑内联，直接 resume
                if (error != null) {
                    Log.w("UserInfoService", error)
                    cont.resume(parseLocalData(), onCancellation = null)
                } else if (response != null) {
                    try {
                        val obj = Gson().fromJson(response, UserInfoResponse::class.java)
                        cont.resume(obj, onCancellation = null)
                    } catch (e: Exception) {
                        Log.e("UserInfoService", "JSON 解析失败，使用本地数据", e)
                        cont.resume(parseLocalData(), onCancellation = null)
                    }
                } else {
                    Log.w("UserInfoService", "收到空响应，使用本地数据")
                    cont.resume(parseLocalData(), onCancellation = null)
                }
            }
        }

        // 2. 挂起地写缓存
        parsed?.data?.let { repo.cacheUser(it) }

        // 3. 返回结果
        parsed
    }
    // endregion

    // region 响应处理
    private fun handleResponse(
        response: String?,
        error: Throwable?,
        callback: (UserInfoResponse?) -> Unit
    ) {
        when {
            error != null -> {
                Log.w("UserInfoService", "网络请求失败，使用本地数据")
                callback(parseLocalData())
            }

            response != null -> {
                try {
                    callback(Gson().fromJson(response, UserInfoResponse::class.java))
                } catch (e: Exception) {
                    Log.e("UserInfoService", "JSON 解析失败", e)
                    callback(parseLocalData())
                }
            }

            else -> {
                Log.w("UserInfoService", "收到空响应，使用本地数据")
                callback(parseLocalData())
            }
        }
    }

    private fun parseLocalData(): UserInfoResponse {
        return Gson().fromJson(localResponse, UserInfoResponse::class.java)
    }
    // endregion
}