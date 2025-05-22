package com.atcumt.kxq.page.profile.dao

import android.util.Log
import com.atcumt.kxq.utils.network.user.info.me.UserInfoService.UserInfoData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    /** 将网络层 DTO 转为 Entity 并持久化 */
    suspend fun cacheUser(data: UserInfoData) {
        val user = data.toEntity()
        val statuses = data.statuses?.map { it.toEntity(data.userId) }
        Log.d("UserRepository", "📥 cacheUser start: user=${user.userId}, statuses=${statuses?.size}")
        userDao.upsertUserWithStatuses(user, statuses)
    }

    /** 观察本地缓存，UI 可直接 collect */
    fun observeMe(uid: String): Flow<UserWithStatuses?> =
        userDao.observeUser(uid)

    /** 获取当前所有本地 UserEntity */
    suspend fun fetchAllUsers(): List<UserEntity> =
        userDao.getAllUsers()

    /** 获取当前所有本地 StatusEntity */
    suspend fun fetchAllStatuses(): List<StatusEntity> =
        userDao.getAllStatuses()
}

// ――― DTO ↔ Entity 转换扩展函数 ―――
private fun UserInfoData.toEntity() = UserEntity(
    userId = userId,
    username = username,
    nickname = nickname,
    avatar = avatar,
    bio = bio,
    gender = gender,
    hometown = hometown,
    major = major,
    grade = grade,
    level = level,
    experience = experience,
    followersCount = followersCount,
    followingsCount = followingsCount,
    likeReceivedCount = likeReceivedCount
)

private fun com.atcumt.kxq.utils.network.user.info.me.UserInfoService.StatusData
        .toEntity(userId: String) = StatusEntity(
    userId = userId,
    emoji = emoji,
    text = text,
    endTime = endTime
)
