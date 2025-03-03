package com.atcumt.kxq.utils.network.user.info.me

import android.util.Log
import com.atcumt.kxq.page.login.dao.StatusEntity
import com.atcumt.kxq.page.login.dao.UserDao
import com.atcumt.kxq.page.login.dao.UserEntity
import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_USER
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class UserInfoService(private val userDao: UserDao) {

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

    // 本地数据
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

    // 获取用户信息
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    suspend fun getUserInfoBlocking(token: String): UserInfoResponse? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val headers = mapOf(
                "Accept" to "application/json",
                "Authorization" to "Bearer $token"
            )

            // 调用 ApiServiceS 的 GET 方法
            ApiServiceS.get(
                BASE_URL_USER,
                "info/v1/me",
                headers = headers
            ) { response, error ->
                if (error != null) {
                    // 网络请求失败时，返回本地数据
                    val localParsedResponse = parseUserInfoResponse(localResponse)
                    continuation.resume(localParsedResponse, null)
                } else {
                    // 网络请求成功时，解析响应
                    val parsedResponse = response?.let { parseUserInfoResponse(it) }
                    // 直接在协程中调用 saveToLocal
                    GlobalScope.launch(Dispatchers.IO) {
                        parsedResponse?.data?.let { saveToLocal(it) }
                        continuation.resume(parsedResponse, null)
                    }
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

    // 保存用户信息到本地数据库
    private suspend fun saveToLocal(data: UserInfoData) = withContext(Dispatchers.IO) {
        // 转换为主实体
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

        // 转换状态数据
        val statusEntities = data.statuses?.mapNotNull { status ->
            status?.let {
                StatusEntity(
                    userId = data.userId,
                    emoji = it.emoji,
                    text = it.text,
                    endTime = it.endTime
                )
            }
        }

        // 事务操作
        userDao.runInTransaction {
            // 更新用户信息
            userDao.upsertUser(userEntity)

            // 更新状态信息（先删除旧数据）
            userDao.deleteStatusByUser(data.userId)

            // 插入新状态
            statusEntities?.let { userDao.insertStatuses(it) }
        }
    }
}