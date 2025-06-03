package com.example.project

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore
) {
    suspend fun getLocalServices(): List<Service> {
        return db.serviceDao().getAllServices().firstOrNull() ?: emptyList()
    }

    suspend fun initializeSampleData() {
        if (db.serviceDao().getAllServices().firstOrNull().isNullOrEmpty()) {
            val sampleServices = listOf(
                Service(
                    id = "1",
                    title = "Архитектурное проектирование",
                    description = "Разработка индивидуального проекта дома с 3D-визуализацией",
                    price = 50000.0,
                    imageUrl = "https://sapr-soft.ru/sites/default/files/inline-images/%D0%90%D1%80%D1%85%D0%B8%D1%82%D0%B5%D0%BA%D1%82%D1%83%D1%80%D0%BD%D0%BE%D0%B5%20%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-min.jpg",
                    durationDays = 14
                ),
                Service(
                    id = "2",
                    title = "Строительство коттеджа",
                    description = "Под ключ: фундамент, коробка, кровля, коммуникации",
                    price = 35000000.0,
                    imageUrl = "https://goodstone.ru/images/v3/stroikey/bgv2m.png",
                    area = 150.0
                ),
                Service(
                    id = "3",
                    title = "Ремонт квартиры",
                    description = "Комплексный ремонт с материалами: демонтаж, черновая и чистовая отделка",
                    price = 120000.0,
                    imageUrl = "https://evdom.ru/wp-content/uploads/2018/08/1-8-1000x563.jpg",
                    area = 80.0
                ),
                Service(
                    id = "4",
                    title = "Электромонтажные работы",
                    description = "Полная разводка электрики по квартире/дому",
                    price = 2500.0,
                    imageUrl = "https://myremont.ru/assets/from_origin/upload/resize_cache/iblock/c79/600_450_2/c79529ac5a34b715f6c129b9525788a4.jpg",
                    area = 100.0
                ),
                Service(
                    id = "5",
                    title = "Сантехнические работы",
                    description = "Разводка труб, установка сантехники, подключение бытовых приборов",
                    price = 1800.0,
                    imageUrl = "https://www.007spb.ru/images/santeh4.jpg",
                    area = 120.0
                ),
                Service(
                    id = "6",
                    title = "Дизайн-проект интерьера",
                    description = "3D-визуализация, подбор материалов, авторский надзор",
                    price = 30000.0,
                    imageUrl = "https://remplanner.ru/images/studio/layout/planning.jpg",
                    durationDays = 21
                ),
                Service(
                    id = "7",
                    title = "Устройство фундамента",
                    description = "Ленточный фундамент с гидроизоляцией",
                    price = 800000.0,
                    imageUrl = "https://drev-brus.ru/wp-content/uploads/1-41.jpg",
                    area = 200.0
                ),
                Service(
                    id = "8",
                    title = "Монтаж кровли",
                    description = "Устройство кровли с утеплением",
                    price = 25000.0,
                    imageUrl = "https://www.pkksib.ru/assets/images/webp/5e856a48dae75.webp",
                    area = 180.0
                )
            )
            db.serviceDao().insertAll(sampleServices)
        }
    }

    suspend fun addUserService(userId: String, serviceId: String) {
        val crossRef = UserServiceCrossRef(userId, serviceId)
        db.userServiceCrossRefDao().insertUserServiceCrossRef(crossRef)
    }

    suspend fun fetchServicesFromFirebase(): List<Service> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("services").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    Service(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun syncServices() {
        val remoteServices = fetchServicesFromFirebase()
        if (remoteServices.isNotEmpty()) {
            db.serviceDao().insertAll(remoteServices)
        }
    }
}