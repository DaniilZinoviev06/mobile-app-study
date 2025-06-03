package com.example.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val email: String,
    var displayName: String? = null,
    var photoUrl: String? = null,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val lastUpdated: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", null, null, 0L)
}