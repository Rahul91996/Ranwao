package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminLogEntity
import com.example.data.model.AiProcessingEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {

    // USERS
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isPro = :isPro, subscriptionStatus = :status, plan = :plan, subscriptionStart = :start, subscriptionExpiry = :expiry WHERE id = :userId")
    suspend fun updateUserSubscription(
        userId: String,
        isPro: Boolean,
        status: String,
        plan: String,
        start: Long?,
        expiry: Long?
    )

    @Query("UPDATE users SET freeEditsRemaining = :freeEdits WHERE id = :userId")
    suspend fun updateUserFreeEdits(userId: String, freeEdits: Int)

    @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :userId")
    suspend fun setUserSuspended(userId: String, isSuspended: Boolean)


    // PAYMENTS
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId LIMIT 1")
    suspend fun getPaymentById(paymentId: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPaymentsByUser(userId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("UPDATE payments SET status = :status, approvedAt = :approvedAt, approvedBy = :approvedBy, rejectionReason = :rejectionReason WHERE paymentId = :paymentId")
    suspend fun updatePaymentStatus(
        paymentId: String,
        status: String,
        approvedAt: Long?,
        approvedBy: String?,
        rejectionReason: String?
    )


    // AI PROCESSING LOGS
    @Query("SELECT * FROM ai_processing ORDER BY createdAt DESC")
    fun getAllAiProcessing(): Flow<List<AiProcessingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiProcessing(log: AiProcessingEntity)


    // ADMIN ACTIVITY LOGS
    @Query("SELECT * FROM admin_logs ORDER BY createdAt DESC")
    fun getAllAdminLogs(): Flow<List<AdminLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminLog(log: AdminLogEntity)


    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 'ALL' OR (userId = 'PREMIUM' AND :isPro = 1) OR (userId = 'FREE' AND :isPro = 0) ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String, isPro: Boolean): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)


    // SUBSCRIPTION PLANS
    @Query("SELECT * FROM subscription_plans")
    fun getAllPlans(): Flow<List<SubscriptionPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: SubscriptionPlanEntity)

    @Update
    suspend fun updatePlan(plan: SubscriptionPlanEntity)


    // APP SETTINGS
    @Query("SELECT * FROM app_settings WHERE id = 'default_settings' LIMIT 1")
    fun getAppSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 'default_settings' LIMIT 1")
    suspend fun getAppSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(settings: AppSettingsEntity)
}
