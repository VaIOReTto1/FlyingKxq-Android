package com.atcumt.kxq.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.atcumt.kxq.page.login.dao.StatusEntity
import com.atcumt.kxq.page.login.dao.UserDao
import com.atcumt.kxq.page.login.dao.UserEntity

// RoomDatabase 初始化示例
@Database(entities = [UserEntity::class, StatusEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
