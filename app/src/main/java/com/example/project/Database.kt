package com.example.project

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Service::class, UserServiceCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun userServiceCrossRefDao(): UserServiceCrossRefDao
}