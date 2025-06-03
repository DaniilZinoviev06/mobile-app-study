package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class Service(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String? = null,
    val area: Double? = null,
    val durationDays: Int? = null,
    val materialsIncluded: Boolean = false
)
