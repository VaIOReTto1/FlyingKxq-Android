package com.atcumt.kxq.page.profile.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

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

@Entity(
    tableName = "status",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ Index("userId") ]    // 加个索引，加速关联查询
)
data class StatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,              // 自增主键
    val userId: String,
    val emoji: String?,
    val text: String?,
    val endTime: String?            // 建议后续改为 Long/Instant 并写 TypeConverter
)

data class UserWithStatuses(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val statuses: List<StatusEntity>
)

@Dao
interface UserDao {

    // ――― 写操作 ―――

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatuses(statuses: List<StatusEntity>)

    /** 原子 upsert，避免两次 I/O */
    @Transaction
    suspend fun upsertUserWithStatuses(
        user: UserEntity,
        statuses: List<StatusEntity>?
    ) {
        insertUser(user)
        statuses?.let { insertStatuses(it) }
    }

    // ――― 读操作 ―――

    /** UI 订阅：只关心当前登录用户，可返回 null */
    @Transaction
    @Query("SELECT * FROM users WHERE userId = :uid LIMIT 1")
    fun observeUser(uid: String): Flow<UserWithStatuses?>

    @Query("DELETE FROM users")      // demo：清空所有用户
    suspend fun clearAll()

    // 查询所有用户
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    // 查询所有状态
    @Query("SELECT * FROM status")
    suspend fun getAllStatuses(): List<StatusEntity>

    @Query("DELETE FROM status")
    suspend fun clearAllStatuses()
}