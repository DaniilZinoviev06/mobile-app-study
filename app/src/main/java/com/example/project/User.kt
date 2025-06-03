package com.example.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val email: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val lastUpdated: Long = System.currentTimeMillis(),
    @Transient var displayName: String? = null,
    @Transient var photoUrl: String? = null
)
