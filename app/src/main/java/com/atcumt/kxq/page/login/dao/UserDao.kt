package com.atcumt.kxq.page.login.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

// UserEntity.kt
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val nickname: String?,
    val avatar: String?,
    val bio: String?,
    val gender: Int?,
    val hometown: String?,
    val major: String?,
    val grade: Int?,
    val level: Int?,
    val experience: Int?,
    val followersCount: Int?,
    val followingsCount: Int?,
    val likeReceivedCount: Int?
)

// StatusEntity.kt
@Entity(
    tableName = "statuses",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class StatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val emoji: String?,
    val text: String?,
    val endTime: String?
)

// UserDao.kt
@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Insert
    suspend fun insertStatuses(statuses: List<StatusEntity>)

    @Query("DELETE FROM statuses WHERE userId = :userId")
    suspend fun deleteStatusByUser(userId: String)

    @Transaction
    suspend fun runInTransaction(block: suspend () -> Unit) {
        block()
    }

    // 其他查询方法...
}