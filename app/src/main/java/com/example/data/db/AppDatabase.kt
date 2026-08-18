package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AdminLogEntity
import com.example.data.model.AiProcessingEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        ProjectEntity::class,
        UserEntity::class,
        PaymentEntity::class,
        AiProcessingEntity::class,
        AdminLogEntity::class,
        NotificationEntity::class,
        SubscriptionPlanEntity::class,
        AppSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun adminDao(): AdminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rewivo_ai_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
