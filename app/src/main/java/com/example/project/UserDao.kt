package com.example.project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUser(userId: String): User?

    @Query("DELETE FROM users WHERE uid = :userId")
    suspend fun deleteUser(userId: String)
}