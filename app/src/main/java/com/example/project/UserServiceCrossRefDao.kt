package com.example.project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserServiceCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserServiceCrossRef(crossRef: UserServiceCrossRef)

    @Query("""
        SELECT services.* FROM services 
        INNER JOIN user_service_cross_ref ON services.id = user_service_cross_ref.serviceId 
        WHERE user_service_cross_ref.userId = :userId
    """)
    fun getServicesForUser(userId: String): Flow<List<Service>>

    @Query("""
        SELECT users.* FROM users 
        INNER JOIN user_service_cross_ref ON users.uid = user_service_cross_ref.userId 
        WHERE user_service_cross_ref.serviceId = :serviceId
    """)
    fun getUsersForService(serviceId: String): Flow<List<User>>
}