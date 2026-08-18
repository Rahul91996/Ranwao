package com.example.data.repository

import com.example.data.db.AdminDao
import com.example.data.model.AdminLogEntity
import com.example.data.model.AiProcessingEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

sealed class AdminAuthResult {
    data class Success(val email: String, val role: String, val sessionToken: String) : AdminAuthResult()
    data class AccessDenied(val message: String) : AdminAuthResult()
    data class Error(val message: String) : AdminAuthResult()
}

class AdminRepository(private val adminDao: AdminDao) {

    companion object {
        const val AUTHORIZED_SUPER_ADMIN_EMAIL = "piyushkumar44397@gmail.com"
        const val ROLE_SUPER_ADMIN = "SUPER_ADMIN"
    }

    // Backend Authenticated Session State
    private var currentSessionToken: String? = null
    private var currentAdminEmail: String? = null
    private var currentAdminRole: String? = null
    private var isSessionActive: Boolean = false

    val allUsers: Flow<List<UserEntity>>
        get() {
            return if (isSessionActive && currentAdminEmail == AUTHORIZED_SUPER_ADMIN_EMAIL && currentAdminRole == ROLE_SUPER_ADMIN) {
                adminDao.getAllUsers()
            } else {
                emptyFlow()
            }
        }

    val allPayments: Flow<List<PaymentEntity>>
        get() {
            return if (isSessionActive && currentAdminEmail == AUTHORIZED_SUPER_ADMIN_EMAIL && currentAdminRole == ROLE_SUPER_ADMIN) {
                adminDao.getAllPayments()
            } else {
                emptyFlow()
            }
        }

    val allAiProcessing: Flow<List<AiProcessingEntity>>
        get() {
            return if (isSessionActive && currentAdminEmail == AUTHORIZED_SUPER_ADMIN_EMAIL && currentAdminRole == ROLE_SUPER_ADMIN) {
                adminDao.getAllAiProcessing()
            } else {
                emptyFlow()
            }
        }

    val allAdminLogs: Flow<List<AdminLogEntity>>
        get() {
            return if (isSessionActive && currentAdminEmail == AUTHORIZED_SUPER_ADMIN_EMAIL && currentAdminRole == ROLE_SUPER_ADMIN) {
                adminDao.getAllAdminLogs()
            } else {
                emptyFlow()
            }
        }

    val allNotifications: Flow<List<NotificationEntity>>
        get() {
            return if (isSessionActive && currentAdminEmail == AUTHORIZED_SUPER_ADMIN_EMAIL && currentAdminRole == ROLE_SUPER_ADMIN) {
                adminDao.getAllNotifications()
            } else {
                emptyFlow()
            }
        }

    val allPlans: Flow<List<SubscriptionPlanEntity>> = adminDao.getAllPlans()
    val appSettings: Flow<AppSettingsEntity?> = adminDao.getAppSettingsFlow()

    fun isSuperAdminAuthenticated(): Boolean {
        return isSessionActive &&
                currentAdminEmail?.equals(AUTHORIZED_SUPER_ADMIN_EMAIL, ignoreCase = true) == true &&
                currentAdminRole == ROLE_SUPER_ADMIN
    }

    fun getAuthenticatedAdminEmail(): String? = currentAdminEmail
    fun getAuthenticatedAdminRole(): String? = currentAdminRole

    suspend fun authenticateAdminSession(email: String, authMethod: String = "Email/Password"): AdminAuthResult {
        val normalizedEmail = email.trim().lowercase()

        // BACKEND VERIFICATION RULE: Only piyushkumar44397@gmail.com is authorized
        if (normalizedEmail == AUTHORIZED_SUPER_ADMIN_EMAIL) {
            val token = "sess_admin_${System.currentTimeMillis()}_${(1000..9999).random()}"
            currentSessionToken = token
            currentAdminEmail = AUTHORIZED_SUPER_ADMIN_EMAIL
            currentAdminRole = ROLE_SUPER_ADMIN
            isSessionActive = true

            // Log successful SUPER_ADMIN authentication
            adminDao.insertAdminLog(
                AdminLogEntity(
                    logId = "log_auth_${System.currentTimeMillis()}",
                    adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                    adminName = "Piyush Kumar (Super Admin)",
                    action = "SUPER_ADMIN Login Successful",
                    details = "Authenticated via $authMethod as SUPER_ADMIN ($AUTHORIZED_SUPER_ADMIN_EMAIL)"
                )
            )

            return AdminAuthResult.Success(
                email = AUTHORIZED_SUPER_ADMIN_EMAIL,
                role = ROLE_SUPER_ADMIN,
                sessionToken = token
            )
        } else {
            // Log Access Denied Security Violation
            adminDao.insertAdminLog(
                AdminLogEntity(
                    logId = "log_sec_${System.currentTimeMillis()}",
                    adminId = normalizedEmail,
                    adminName = "Unauthorized Attempt",
                    action = "Admin Access Denied",
                    details = "Rejected unauthorized admin panel login attempt from: $email"
                )
            )

            return AdminAuthResult.AccessDenied(
                message = "You are not authorized to access the REWIVO AI Admin Panel."
            )
        }
    }

    fun invalidateSession() {
        isSessionActive = false
        currentSessionToken = null
        currentAdminEmail = null
        currentAdminRole = null
    }

    private fun verifyBackendSuperAdminAccess() {
        if (!isSuperAdminAuthenticated()) {
            throw SecurityException("Access Denied: You are not authorized to access the REWIVO AI Admin Panel.")
        }
    }

    suspend fun initializeDefaultsIfEmpty() {
        // App Settings
        if (adminDao.getAppSettings() == null) {
            adminDao.insertAppSettings(AppSettingsEntity())
        }

        // Subscription Plans
        val existingPlans = adminDao.getAllPlans()
        // Insert default plans if not exists
        adminDao.insertPlan(
            SubscriptionPlanEntity(
                planId = "pro_monthly",
                name = "PRO Monthly",
                price = 99.0,
                currency = "₹",
                interval = "Month",
                durationDays = 30,
                isEnabled = true,
                benefits = "Unlimited background changes\nHD/4K export\nAI Background Generator\nPremium backgrounds\nNo watermark\nFaster processing"
            )
        )
        adminDao.insertPlan(
            SubscriptionPlanEntity(
                planId = "pro_yearly",
                name = "PRO Yearly",
                price = 499.0,
                currency = "₹",
                interval = "Year",
                durationDays = 365,
                isEnabled = true,
                benefits = "Unlimited background changes\nHD/4K export\nAI Background Generator\nPremium backgrounds\nNo watermark\nFaster 5x priority AI processing\nSave 60%"
            )
        )

        // Default Users
        if (adminDao.getUserById("usr_9981") == null) {
            adminDao.insertUser(
                UserEntity(
                    id = "usr_9981",
                    name = "Alex Rivera",
                    email = "alex.rivera@example.com",
                    freeEditsRemaining = 3,
                    subscriptionStatus = "Free",
                    plan = "Free",
                    isPro = false,
                    totalAiEdits = 5,
                    createdAt = System.currentTimeMillis() - 864000000L
                )
            )
        }
        if (adminDao.getUserById("usr_10245") == null) {
            adminDao.insertUser(
                UserEntity(
                    id = "usr_10245",
                    name = "Rahul Sharma",
                    email = "rahul.sharma@example.com",
                    freeEditsRemaining = 0,
                    subscriptionStatus = "Pending Verification",
                    plan = "PRO Monthly",
                    isPro = false,
                    totalAiEdits = 12,
                    createdAt = System.currentTimeMillis() - 432000000L
                )
            )
        }

        // Sample Payment Request if empty
        if (adminDao.getPaymentById("pay_1001") == null) {
            adminDao.insertPayment(
                PaymentEntity(
                    paymentId = "pay_1001",
                    userId = "usr_10245",
                    userName = "Rahul Sharma",
                    userEmail = "rahul.sharma@example.com",
                    transactionId = "TXN9872145300",
                    plan = "PRO Monthly",
                    amount = 99.0,
                    paymentMethod = "UPI (GPay/PhonePe)",
                    status = "Pending",
                    createdAt = System.currentTimeMillis() - 3600000L
                )
            )
        }

        // Sample Processing Logs if empty
        adminDao.insertAiProcessing(
            AiProcessingEntity(
                processingId = "proc_801",
                userId = "usr_10245",
                userName = "Rahul Sharma",
                mediaType = "PHOTO",
                processingType = "Background Swap",
                startTime = System.currentTimeMillis() - 7200000L,
                durationMs = 1240,
                status = "Completed"
            )
        )
        adminDao.insertAiProcessing(
            AiProcessingEntity(
                processingId = "proc_802",
                userId = "usr_9981",
                userName = "Alex Rivera",
                mediaType = "VIDEO",
                processingType = "AI Background Generator",
                startTime = System.currentTimeMillis() - 3600000L,
                durationMs = 3800,
                status = "Completed"
            )
        )

        // Admin Activity Log
        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_init",
                adminId = "admin_root",
                adminName = "Super Admin",
                action = "System Initialized",
                details = "REWIVO AI Admin Panel and Database initialized securely."
            )
        )
    }

    // PAYMENT APPROVAL
    suspend fun approvePayment(paymentId: String, adminName: String = "Piyush Kumar (Super Admin)"): Boolean {
        verifyBackendSuperAdminAccess()
        val payment = adminDao.getPaymentById(paymentId) ?: return false
        val now = System.currentTimeMillis()

        // 1. Update Payment Status to Approved
        adminDao.updatePaymentStatus(
            paymentId = paymentId,
            status = "Approved",
            approvedAt = now,
            approvedBy = adminName,
            rejectionReason = null
        )

        // 2. Determine duration based on plan
        val durationDays = if (payment.plan.contains("Yearly", ignoreCase = true)) 365 else 30
        val expiryDate = now + (durationDays * 24 * 60 * 60 * 1000L)

        // 3. Update User's Subscription Status to Premium
        val user = adminDao.getUserById(payment.userId)
        val updatedUser = user?.copy(
            isPro = true,
            subscriptionStatus = payment.plan,
            plan = payment.plan,
            subscriptionStart = now,
            subscriptionExpiry = expiryDate
        ) ?: UserEntity(
            id = payment.userId,
            name = payment.userName,
            email = payment.userEmail,
            isPro = true,
            subscriptionStatus = payment.plan,
            plan = payment.plan,
            subscriptionStart = now,
            subscriptionExpiry = expiryDate
        )
        adminDao.insertUser(updatedUser)

        // 4. Create Notification for User
        adminDao.insertNotification(
            NotificationEntity(
                notificationId = "notif_${System.currentTimeMillis()}",
                userId = payment.userId,
                title = "🎉 REWIVO AI PRO Activated!",
                message = "Your ${payment.plan} subscription (₹${payment.amount.toInt()}) has been verified and activated. Enjoy unlimited HD edits!",
                type = "Payment Approved"
            )
        )

        // 5. Audit Log
        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = "admin_root",
                adminName = adminName,
                action = "Payment Approved",
                targetUserId = payment.userId,
                targetUserName = payment.userName,
                details = "Approved ₹${payment.amount.toInt()} payment (Txn ID: ${payment.transactionId}) for ${payment.plan}."
            )
        )

        return true
    }

    // PAYMENT REJECTION
    suspend fun rejectPayment(paymentId: String, reason: String?, adminName: String = "Piyush Kumar (Super Admin)"): Boolean {
        verifyBackendSuperAdminAccess()
        val payment = adminDao.getPaymentById(paymentId) ?: return false

        val rejectionText = reason?.ifBlank { null } ?: "Payment verification failed. Please check transaction ID."

        // 1. Update Payment Status to Rejected
        adminDao.updatePaymentStatus(
            paymentId = paymentId,
            status = "Rejected",
            approvedAt = null,
            approvedBy = adminName,
            rejectionReason = rejectionText
        )

        // 2. Create Notification for User
        adminDao.insertNotification(
            NotificationEntity(
                notificationId = "notif_${System.currentTimeMillis()}",
                userId = payment.userId,
                title = "❌ Payment Verification Failed",
                message = "Your payment could not be verified ($rejectionText). Please check your payment details and try again.",
                type = "Payment Rejected"
            )
        )

        // 3. Audit Log
        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = "Payment Rejected",
                targetUserId = payment.userId,
                targetUserName = payment.userName,
                details = "Rejected payment (Txn ID: ${payment.transactionId}). Reason: $rejectionText"
            )
        )

        return true
    }

    // SUBMIT USER PAYMENT REQUEST
    suspend fun createPaymentRequest(
        user: UserEntity,
        plan: String,
        amount: Double,
        transactionId: String,
        paymentMethod: String,
        proofUrl: String? = null
    ): PaymentEntity {
        val paymentId = "pay_${System.currentTimeMillis()}"
        val payment = PaymentEntity(
            paymentId = paymentId,
            userId = user.id,
            userName = user.name,
            userEmail = user.email,
            transactionId = transactionId,
            plan = plan,
            amount = amount,
            paymentMethod = paymentMethod,
            status = "Pending",
            paymentProofUrl = proofUrl,
            createdAt = System.currentTimeMillis()
        )
        adminDao.insertPayment(payment)

        // Ensure user is recorded
        adminDao.insertUser(user)

        return payment
    }

    // MANUAL PREMIUM CONTROL
    suspend fun grantPremiumManually(
        userId: String,
        plan: String,
        durationDays: Int = 30,
        adminName: String = "Piyush Kumar (Super Admin)"
    ) {
        verifyBackendSuperAdminAccess()
        val user = adminDao.getUserById(userId) ?: return
        val now = System.currentTimeMillis()
        val expiry = now + (durationDays * 24 * 60 * 60 * 1000L)

        val updated = user.copy(
            isPro = true,
            subscriptionStatus = plan,
            plan = plan,
            subscriptionStart = now,
            subscriptionExpiry = expiry
        )
        adminDao.insertUser(updated)

        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = "Manual Premium Granted",
                targetUserId = userId,
                targetUserName = user.name,
                details = "Manually granted $plan for $durationDays days."
            )
        )

        adminDao.insertNotification(
            NotificationEntity(
                notificationId = "notif_${System.currentTimeMillis()}",
                userId = userId,
                title = "🎉 REWIVO PRO Access Granted",
                message = "An administrator has granted you access to REWIVO AI PRO ($plan).",
                type = "Premium Activated"
            )
        )
    }

    suspend fun removePremiumManually(userId: String, adminName: String = "Piyush Kumar (Super Admin)") {
        verifyBackendSuperAdminAccess()
        val user = adminDao.getUserById(userId) ?: return
        val updated = user.copy(
            isPro = false,
            subscriptionStatus = "Free",
            plan = "Free",
            subscriptionStart = null,
            subscriptionExpiry = null
        )
        adminDao.insertUser(updated)

        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = "Manual Premium Removed",
                targetUserId = userId,
                targetUserName = user.name,
                details = "Manually removed PRO subscription."
            )
        )
    }

    // USER SUSPENSION
    suspend fun toggleUserSuspension(userId: String, isSuspended: Boolean, adminName: String = "Piyush Kumar (Super Admin)") {
        verifyBackendSuperAdminAccess()
        val user = adminDao.getUserById(userId) ?: return
        adminDao.setUserSuspended(userId, isSuspended)

        val action = if (isSuspended) "User Suspended" else "User Activated"
        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = action,
                targetUserId = userId,
                targetUserName = user.name,
                details = if (isSuspended) "Account suspended by admin." else "Account reactivated by admin."
            )
        )
    }

    // RESET FREE EDITS
    suspend fun resetFreeEdits(userId: String, editCount: Int = 3, adminName: String = "Piyush Kumar (Super Admin)") {
        verifyBackendSuperAdminAccess()
        val user = adminDao.getUserById(userId) ?: return
        adminDao.updateUserFreeEdits(userId, editCount)

        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = "Free Edits Reset",
                targetUserId = userId,
                targetUserName = user.name,
                details = "Reset free edits to $editCount."
            )
        )
    }

    // BROADCAST NOTIFICATION
    suspend fun sendBroadcastNotification(
        targetGroup: String, // "ALL", "PREMIUM", "FREE", or userId
        title: String,
        message: String,
        adminName: String = "Piyush Kumar (Super Admin)"
    ) {
        verifyBackendSuperAdminAccess()
        val notif = NotificationEntity(
            notificationId = "notif_${System.currentTimeMillis()}",
            userId = targetGroup,
            title = title,
            message = message,
            type = "System Announcement"
        )
        adminDao.insertNotification(notif)

        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = "Notification Broadcast",
                details = "Sent notification to $targetGroup: \"$title\""
            )
        )
    }

    // RECORD AI PROCESSING LOG
    suspend fun logAiProcessing(
        userId: String,
        userName: String,
        mediaType: String,
        processingType: String,
        durationMs: Long,
        status: String,
        errorMessage: String? = null
    ) {
        val log = AiProcessingEntity(
            processingId = "proc_${System.currentTimeMillis()}",
            userId = userId,
            userName = userName,
            mediaType = mediaType,
            processingType = processingType,
            startTime = System.currentTimeMillis() - durationMs,
            durationMs = durationMs,
            status = status,
            errorMessage = errorMessage
        )
        adminDao.insertAiProcessing(log)
    }

    // UPDATE PLAN
    suspend fun updateSubscriptionPlan(plan: SubscriptionPlanEntity, adminName: String = "Piyush Kumar (Super Admin)") {
        verifyBackendSuperAdminAccess()
        adminDao.updatePlan(plan)
        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = "Plan Modified",
                details = "Updated plan ${plan.name} price to ₹${plan.price}."
            )
        )
    }

    // UPDATE APP SETTINGS
    suspend fun updateAppSettings(settings: AppSettingsEntity, adminName: String = "Piyush Kumar (Super Admin)") {
        verifyBackendSuperAdminAccess()
        adminDao.insertAppSettings(settings)
        adminDao.insertAdminLog(
            AdminLogEntity(
                logId = "log_${System.currentTimeMillis()}",
                adminId = AUTHORIZED_SUPER_ADMIN_EMAIL,
                adminName = adminName,
                action = "App Settings Updated",
                details = "Updated app name: ${settings.appName}, maintenance mode: ${settings.maintenanceMode}."
            )
        )
    }
}
