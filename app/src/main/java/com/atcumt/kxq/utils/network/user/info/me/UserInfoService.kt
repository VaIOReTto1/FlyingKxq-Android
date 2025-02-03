package com.atcumt.kxq.utils.network.user.info.me

import android.content.Context
import android.util.Log
import com.atcumt.kxq.utils.network.ApiService
import com.atcumt.kxq.utils.network.auth.login.LoginService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class UserInfoService : ApiService() {

    // 本地存储工具类
//    private val sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)

    // 响应数据结构
    data class UserInfoResponse(
        val code: Int,
        val msg: String,
        val data: UserInfoData?
    )

    data class UserInfoData(
        val userId: String,
        val username: String,
        val nickname: String?,
        val avatar: String?,
        val bio: String?,
        val gender: Int?,
        val hometown: String?,
        val major: String?,
        val grade: Int?,
        val statuses: List<StatusData?>?,
        val level: Int?,
        val experience: Int?,
        val followersCount: Int?,
        val followingsCount: Int?,
        val likeReceivedCount: Int?
    )

    data class StatusData(
        val emoji: String?,
        val text: String?,
        val endTime: String?
    )

    // 获取用户信息
    fun getUserInfo(
        token: String,
        callback: (UserInfoResponse?, IOException?) -> Unit
    ) {
        val headers = Headers.Builder()
            .add("Accept", "application/json")
            .add("Authorization", "Bearer $token")
            .build()

        val url = buildUrlWithParams(BASE_URL_USER, "info/v1/me")

        get(url, headers) { response, error ->
            Log.d("NetworkLog", "getUserInfo: $response")
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseUserInfoResponse(it) }
                parsedResponse?.data?.let { saveToLocal(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 修改 UserInfoService 的 getUserInfoBlocking 方法
    suspend fun getUserInfoBlocking(token:String): UserInfoResponse = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            getUserInfo(token) { response, error ->
                when {
                    error != null -> continuation.resumeWith(Result.failure(error))
                    response != null -> continuation.resumeWith(Result.success(response))
                    else -> continuation.resumeWith(Result.failure(IOException("Unknown error")))
                }
            }
        }
    }

    // 解析响应
    private fun parseUserInfoResponse(jsonString: String): UserInfoResponse {
        val json = JSONObject(jsonString)
        return UserInfoResponse(
            code = json.getInt("code"),
            msg = json.getString("msg"),
            data = json.optJSONObject("data")?.let { dataJson ->
                UserInfoData(
                    userId = dataJson.optString("userId"),
                    username = dataJson.optString("username"),
                    nickname = dataJson.optString("nickname"),
                    avatar = dataJson.optString("avatar"),
                    bio = dataJson.optString("bio"),
                    gender = dataJson.optInt("gender"),
                    hometown = dataJson.optString("hometown"),
                    major = dataJson.optString("major"),
                    grade = dataJson.optInt("grade"),
                    statuses = dataJson.optJSONArray("statuses")?.let { parseStatuses(it) },
                    level = dataJson.optInt("level"),
                    experience = dataJson.optInt("experience"),
                    followersCount = dataJson.optInt("followersCount"),
                    followingsCount = dataJson.optInt("followingsCount"),
                    likeReceivedCount = dataJson.optInt("likeReceivedCount")
                )
            }
        )
    }

    private fun parseStatuses(array: JSONArray): List<StatusData> {
        return List(array.length()) { i ->
            val item = array.optJSONObject(i)
            StatusData(
                emoji = item.optString("emoji"),
                text = item.optString("text"),
                endTime = item.optString("endTime")
            )
        }
    }

    // 本地存储
    private fun saveToLocal(data: UserInfoData) {
//        sharedPreferences.edit().apply {
//            putString("userId", data.userId)
//            putString("username", data.username)
//            putString("nickname", data.nickname)
//            putString("avatar", data.avatar)
//            putString("bio", data.bio)
//            putInt("gender", data.gender)
//            putString("hometown", data.hometown)
//            putString("major", data.major)
//            putInt("grade", data.grade)
//            putString("statuses", JSONArray(data.statuses.map { it.toJson() }).toString())
//            putInt("level", data.level)
//            putInt("experience", data.experience)
//            putInt("followersCount", data.followersCount)
//            putInt("followingsCount", data.followingsCount)
//            putInt("likeReceivedCount", data.likeReceivedCount)
//        }.apply()
    }

    // 状态数据转JSON
    private fun StatusData.toJson(): JSONObject {
        return JSONObject().apply {
            put("emoji", emoji)
            put("text", text)
            put("endTime", endTime)
        }
    }
}