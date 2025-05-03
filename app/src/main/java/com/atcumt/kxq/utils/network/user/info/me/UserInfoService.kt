package com.atcumt.kxq.utils.network.user.info.me

import android.util.Log
import com.atcumt.kxq.page.login.dao.StatusEntity
import com.atcumt.kxq.page.login.dao.UserDao
import com.atcumt.kxq.page.login.dao.UserEntity
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

class UserInfoService(
//    private val userDao: UserDao
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
                "userId": "7bc19f9be50a437f9d9d45193d065b38",
                "username": "qqqqqq",
                "nickname": "圈圈FUknaN",
                "avatar": "http://119.45.93.228:8080/api/file/v1/public/e59b8e263ee1764656e34ef38322f234a67ae9417d66c70652f90311db9271b3.jpg",
                "bio": "",
                "gender": 1,
                "hometown": "",
                "major": null,
                "grade": null,
                "statuses": null,
                "level": 5,
                "experience": 0,
                "followersCount": 2,
                "followingsCount": 0,
                "likeReceivedCount": 0
            }
        }
    """
    // endregion

    // region 网络请求
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    suspend fun getUserInfoBlocking(token: String): UserInfoResponse? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            ApiServiceS.get(
                baseUrl = BASE_URL_USER,
                endpoint = "info/v1/me",
                headers = mapOf(
                    "Accept" to "application/json",
                    "Authorization" to "Bearer $token"
                )
            ) { response, error ->
                handleResponse(response, error) { parsedResponse ->
                    GlobalScope.launch(Dispatchers.IO) {
                        parsedResponse?.data?.let { saveToLocal(it) }
                        continuation.resume(parsedResponse, null)
                    }
                }
            }
        }
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

    // region 数据库操作（保持不变）
    private suspend fun saveToLocal(data: UserInfoData) = withContext(Dispatchers.IO) {
        val userEntity = UserEntity(
            userId = data.userId,
            username = data.username,
            nickname = data.nickname,
            avatar = data.avatar,
            bio = data.bio,
            gender = data.gender,
            hometown = data.hometown,
            major = data.major,
            grade = data.grade,
            level = data.level,
            experience = data.experience,
            followersCount = data.followersCount,
            followingsCount = data.followingsCount,
            likeReceivedCount = data.likeReceivedCount
        )

        val statusEntities = data.statuses?.map {
            it.let { status ->
                StatusEntity(
                    userId = data.userId,
                    emoji = status.emoji,
                    text = status.text,
                    endTime = status.endTime
                )
            }
        }

//        userDao.runInTransaction {
//            userDao.upsertUser(userEntity)
//            userDao.deleteStatusByUser(data.userId)
//            statusEntities?.let { userDao.insertStatuses(it) }
//        }
    }
    // endregion
}