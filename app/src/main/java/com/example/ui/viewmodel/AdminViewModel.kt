package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AdminLogEntity
import com.example.data.model.AiProcessingEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AdminTab {
    DASHBOARD,
    PAYMENTS,
    USERS,
    AI_PROCESSING,
    PROJECTS,
    SUBSCRIPTIONS,
    REVENUE,
    LOGS,
    NOTIFICATIONS,
    SETTINGS
}

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val adminRepository = AdminRepository(db.adminDao())

    // Admin Auth State
    private val _isAdminLoggedIn = MutableStateFlow(false) // Default locked until Super Admin authenticates
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminEmail = MutableStateFlow("")
    val adminEmail: StateFlow<String> = _adminEmail.asStateFlow()

    private val _adminRole = MutableStateFlow<String?>(null)
    val adminRole: StateFlow<String?> = _adminRole.asStateFlow()

    private val _accessDeniedError = MutableStateFlow<String?>(null)
    val accessDeniedError: StateFlow<String?> = _accessDeniedError.asStateFlow()

    private val _currentAdminTab = MutableStateFlow(AdminTab.DASHBOARD)
    val currentAdminTab: StateFlow<AdminTab> = _currentAdminTab.asStateFlow()

    // Flows from Repository
    val allUsers: StateFlow<List<UserEntity>> = adminRepository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = adminRepository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAiProcessing: StateFlow<List<AiProcessingEntity>> = adminRepository.allAiProcessing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAdminLogs: StateFlow<List<AdminLogEntity>> = adminRepository.allAdminLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEntity>> = adminRepository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlans: StateFlow<List<SubscriptionPlanEntity>> = adminRepository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettingsEntity?> = adminRepository.appSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsEntity())

    // Filters & Searches
    private val _userSearchQuery = MutableStateFlow("")
    val userSearchQuery: StateFlow<String> = _userSearchQuery.asStateFlow()

    private val _paymentStatusFilter = MutableStateFlow("ALL") // ALL, PENDING, APPROVED, REJECTED
    val paymentStatusFilter: StateFlow<String> = _paymentStatusFilter.asStateFlow()

    private val _revenueFilter = MutableStateFlow("ALL") // ALL, TODAY, WEEK, MONTH, YEAR
    val revenueFilter: StateFlow<String> = _revenueFilter.asStateFlow()

    // Selected item for detail modals
    private val _selectedPayment = MutableStateFlow<PaymentEntity?>(null)
    val selectedPayment: StateFlow<PaymentEntity?> = _selectedPayment.asStateFlow()

    private val _selectedUser = MutableStateFlow<UserEntity?>(null)
    val selectedUser: StateFlow<UserEntity?> = _selectedUser.asStateFlow()

    private val _adminToast = MutableStateFlow<String?>(null)
    val adminToast: StateFlow<String?> = _adminToast.asStateFlow()

    init {
        viewModelScope.launch {
            adminRepository.initializeDefaultsIfEmpty()
        }
    }

    fun loginAdmin(email: String, pass: String, authMethod: String = "Password") {
        viewModelScope.launch {
            _accessDeniedError.value = null
            when (val result = adminRepository.authenticateAdminSession(email, authMethod)) {
                is com.example.data.repository.AdminAuthResult.Success -> {
                    _adminEmail.value = result.email
                    _adminRole.value = result.role
                    _isAdminLoggedIn.value = true
                    showToast("Welcome SUPER_ADMIN (${result.email})")
                }
                is com.example.data.repository.AdminAuthResult.AccessDenied -> {
                    _isAdminLoggedIn.value = false
                    _accessDeniedError.value = result.message
                    showToast("Access Denied: Unauthorized admin email")
                }
                is com.example.data.repository.AdminAuthResult.Error -> {
                    _isAdminLoggedIn.value = false
                    _accessDeniedError.value = result.message
                    showToast(result.message)
                }
            }
        }
    }

    fun loginWithGoogle(email: String) {
        loginAdmin(email, "GoogleOAuthToken", authMethod = "Google Sign-In")
    }

    fun clearAccessDeniedError() {
        _accessDeniedError.value = null
    }

    fun logoutAdmin() {
        adminRepository.invalidateSession()
        _isAdminLoggedIn.value = false
        _adminRole.value = null
        _adminEmail.value = ""
        _accessDeniedError.value = null
        showToast("Admin session terminated securely.")
    }

    fun setAdminTab(tab: AdminTab) {
        _currentAdminTab.value = tab
    }

    fun setUserSearchQuery(query: String) {
        _userSearchQuery.value = query
    }

    fun setPaymentStatusFilter(filter: String) {
        _paymentStatusFilter.value = filter
    }

    fun setRevenueFilter(filter: String) {
        _revenueFilter.value = filter
    }

    fun selectPayment(payment: PaymentEntity?) {
        _selectedPayment.value = payment
    }

    fun selectUser(user: UserEntity?) {
        _selectedUser.value = user
    }

    fun showToast(msg: String) {
        _adminToast.value = msg
    }

    fun dismissToast() {
        _adminToast.value = null
    }

    // ACTIONS
    fun approvePayment(paymentId: String) {
        viewModelScope.launch {
            val success = adminRepository.approvePayment(paymentId)
            if (success) {
                showToast("✅ Payment approved & Premium activated for user!")
                _selectedPayment.value = null
            } else {
                showToast("Failed to approve payment")
            }
        }
    }

    fun rejectPayment(paymentId: String, reason: String?) {
        viewModelScope.launch {
            val success = adminRepository.rejectPayment(paymentId, reason)
            if (success) {
                showToast("❌ Payment rejected.")
                _selectedPayment.value = null
            } else {
                showToast("Failed to reject payment")
            }
        }
    }

    fun grantPremiumManually(userId: String, plan: String, days: Int) {
        viewModelScope.launch {
            adminRepository.grantPremiumManually(userId, plan, days)
            showToast("Granted $plan to user ($days days)")
            _selectedUser.value = null
        }
    }

    fun removePremiumManually(userId: String) {
        viewModelScope.launch {
            adminRepository.removePremiumManually(userId)
            showToast("Removed Premium from user")
            _selectedUser.value = null
        }
    }

    fun toggleUserSuspension(userId: String, currentSuspended: Boolean) {
        viewModelScope.launch {
            adminRepository.toggleUserSuspension(userId, !currentSuspended)
            val text = if (!currentSuspended) "suspended" else "activated"
            showToast("User account $text")
            _selectedUser.value = null
        }
    }

    fun resetFreeEdits(userId: String, count: Int = 3) {
        viewModelScope.launch {
            adminRepository.resetFreeEdits(userId, count)
            showToast("Reset user free edits to $count")
        }
    }

    fun sendNotification(target: String, title: String, message: String) {
        viewModelScope.launch {
            adminRepository.sendBroadcastNotification(target, title, message)
            showToast("Notification sent to $target")
        }
    }

    fun updatePlan(plan: SubscriptionPlanEntity) {
        viewModelScope.launch {
            adminRepository.updateSubscriptionPlan(plan)
            showToast("Plan updated: ${plan.name} = ₹${plan.price.toInt()}")
        }
    }

    fun updateSettings(settings: AppSettingsEntity) {
        viewModelScope.launch {
            adminRepository.updateAppSettings(settings)
            showToast("App settings saved successfully!")
        }
    }
}
