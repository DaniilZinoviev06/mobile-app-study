package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_service_cross_ref",
    primaryKeys = ["userId", "serviceId"]
)
data class UserServiceCrossRef(
    val userId: String,
    val serviceId: String,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "ожидается"
)
