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
        ORDER BY user_service_cross_ref.orderDate DESC
    """)
    fun getServicesForUser(userId: String): Flow<List<Service>>

    @Query("""
        SELECT * FROM user_service_cross_ref 
        WHERE userId = :userId
        ORDER BY orderDate DESC
    """)
    fun getUserOrders(userId: String): Flow<List<UserServiceCrossRef>>

    @Query("DELETE FROM user_service_cross_ref WHERE userId = :userId AND serviceId = :serviceId")
    suspend fun deleteByUserAndService(userId: String, serviceId: String)
}