package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val profilePhoto: String? = null,
    val freeEditsRemaining: Int = 3,
    val subscriptionStatus: String = "Free", // "Free", "PRO Monthly", "PRO Yearly"
    val plan: String = "Free",
    val isPro: Boolean = false,
    val isSuspended: Boolean = false,
    val subscriptionStart: Long? = null,
    val subscriptionExpiry: Long? = null,
    val totalAiEdits: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val transactionId: String,
    val plan: String, // "PRO Monthly", "PRO Yearly"
    val amount: Double, // 99.0, 499.0
    val paymentMethod: String = "UPI", // "UPI", "Card", "Net Banking", "QR Code"
    val status: String = "Pending", // "Pending", "Approved", "Rejected", "Failed"
    val rejectionReason: String? = null,
    val paymentProofUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedBy: String? = null
)

@Entity(tableName = "ai_processing")
data class AiProcessingEntity(
    @PrimaryKey val processingId: String,
    val userId: String,
    val userName: String,
    val projectId: String? = null,
    val mediaType: String, // "PHOTO", "VIDEO"
    val processingType: String, // "Background Swap", "AI Generator", "Relighting"
    val startTime: Long = System.currentTimeMillis(),
    val durationMs: Long = 0,
    val status: String, // "Queued", "Processing", "Completed", "Failed"
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
    @PrimaryKey val logId: String,
    val adminId: String = "admin_root",
    val adminName: String = "Super Admin",
    val action: String,
    val targetUserId: String? = null,
    val targetUserName: String? = null,
    val details: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val notificationId: String,
    val userId: String, // "ALL", "PREMIUM", "FREE", or specific userId
    val title: String,
    val message: String,
    val type: String, // "Payment Approved", "Payment Rejected", "Premium Activated", "System Announcement"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscription_plans")
data class SubscriptionPlanEntity(
    @PrimaryKey val planId: String, // "pro_monthly", "pro_yearly"
    val name: String, // "PRO Monthly", "PRO Yearly"
    val price: Double, // 99.0, 499.0
    val currency: String = "₹",
    val interval: String = "Month", // "Month", "Year"
    val durationDays: Int = 30, // 30, 365
    val isEnabled: Boolean = true,
    val benefits: String = "Unlimited background changes\nHD/4K export\nAI Background Generator\nPremium backgrounds\nNo watermark\nFaster processing"
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: String = "default_settings",
    val appName: String = "REWIVO AI",
    val supportEmail: String = "support@rewivo.ai",
    val privacyPolicy: String = "https://rewivo.ai/privacy",
    val termsOfService: String = "https://rewivo.ai/terms",
    val freeEditCount: Int = 3,
    val maxFreeVideoDurationSec: Int = 15,
    val maxFileSizeMb: Int = 50,
    val premiumVideoDurationSec: Int = 300,
    val watermarkEnabled: Boolean = true,
    val maintenanceMode: Boolean = false
)
