package com.atcumt.kxq.page.profile.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, StatusEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KxqDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}