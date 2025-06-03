package com.example.project

import android.app.Application
import androidx.room.Room
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()

            firestoreSettings = settings
        }
    }

    override fun onCreate() {
        super.onCreate()
        Glide.init(this, GlideBuilder()
            .setDefaultRequestOptions(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
        )
        Firebase.firestore
    }
}