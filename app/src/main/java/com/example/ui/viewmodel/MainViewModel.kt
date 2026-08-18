package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiBackgroundEngine
import com.example.ai.AiEngineException
import com.example.ai.EditingAdjustments
import com.example.data.db.AppDatabase
import com.example.data.model.BackgroundPreset
import com.example.data.model.MediaType
import com.example.data.model.ProjectEntity
import com.example.data.model.User
import com.example.data.repository.ProjectRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    CREATE,
    PROJECTS,
    PROFILE,
    EDITOR,
    AI_GENERATOR,
    PREMIUM,
    ONBOARDING,
    PAYMENT_CHECKOUT,
    ADMIN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val projectRepository = ProjectRepository(db.projectDao())
    private val userRepository = UserRepository()
    val aiEngine = AiBackgroundEngine(application)

    private val _userState = MutableStateFlow(
        User(
            id = "usr_9981",
            name = "Alex Rivera",
            email = "alex.rivera@example.com",
            isPro = false,
            remainingCredits = 3,
            isLoggedIn = true
        )
    )
    val currentUser: StateFlow<User> = _userState.asStateFlow()

    private val _selectedCheckoutPlanName = MutableStateFlow("PRO Monthly")
    val selectedCheckoutPlanName: StateFlow<String> = _selectedCheckoutPlanName.asStateFlow()

    private val _selectedCheckoutPlanPrice = MutableStateFlow(99.0)
    val selectedCheckoutPlanPrice: StateFlow<Double> = _selectedCheckoutPlanPrice.asStateFlow()

    init {
        // Observe Room User DB to automatically update currentUser state when Admin approves payment or grants PRO
        viewModelScope.launch {
            db.adminDao().getAllUsers().collect { userEntities ->
                val currentEntity = userEntities.find { it.id == _userState.value.id }
                if (currentEntity != null) {
                    _userState.value = _userState.value.copy(
                        name = currentEntity.name,
                        email = currentEntity.email,
                        isPro = currentEntity.isPro,
                        remainingCredits = currentEntity.freeEditsRemaining
                    )
                }
            }
        }
    }

    val projects: StateFlow<List<ProjectEntity>> = projectRepository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active Editor State
    private val _activeMediaType = MutableStateFlow(MediaType.PHOTO)
    val activeMediaType: StateFlow<MediaType> = _activeMediaType.asStateFlow()

    private val _activeMediaUri = MutableStateFlow<String?>(null)
    val activeMediaUri: StateFlow<String?> = _activeMediaUri.asStateFlow()

    private val _activeSourceBitmap = MutableStateFlow<Bitmap?>(null)
    val activeSourceBitmap: StateFlow<Bitmap?> = _activeSourceBitmap.asStateFlow()

    private val _selectedPreset = MutableStateFlow<BackgroundPreset?>(aiEngine.presetBackgrounds.first())
    val selectedPreset: StateFlow<BackgroundPreset?> = _selectedPreset.asStateFlow()

    private val _customBgBitmap = MutableStateFlow<Bitmap?>(null)
    val customBgBitmap: StateFlow<Bitmap?> = _customBgBitmap.asStateFlow()

    private val _adjustments = MutableStateFlow(EditingAdjustments())
    val adjustments: StateFlow<EditingAdjustments> = _adjustments.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress: StateFlow<Float> = _processingProgress.asStateFlow()

    private val _processingStatus = MutableStateFlow("")
    val processingStatus: StateFlow<String> = _processingStatus.asStateFlow()

    private val _editedResultBitmap = MutableStateFlow<Bitmap?>(null)
    val editedResultBitmap: StateFlow<Bitmap?> = _editedResultBitmap.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showPremiumModal = MutableStateFlow(false)
    val showPremiumModal: StateFlow<Boolean> = _showPremiumModal.asStateFlow()

    private val _toastNotification = MutableStateFlow<String?>(null)
    val toastNotification: StateFlow<String?> = _toastNotification.asStateFlow()

    init {
        // Pre-populate sample project if DB is empty
        viewModelScope.launch {
            if (projects.value.isEmpty()) {
                val dummyOriginal = aiEngine.generateSyntheticBackground(aiEngine.presetBackgrounds[0], 600, 600)
                val dummyResult = aiEngine.generateSyntheticBackground(aiEngine.presetBackgrounds[1], 600, 600)
                val pathOriginal = aiEngine.saveBitmapToInternalStorage(dummyOriginal, "sample_original")
                val pathResult = aiEngine.saveBitmapToInternalStorage(dummyResult, "sample_result")

                projectRepository.saveProject(
                    ProjectEntity(
                        title = "Beach Vacation Portrait",
                        mediaType = "PHOTO",
                        originalPath = pathOriginal,
                        editedPath = pathResult,
                        backgroundName = "Beach Paradise",
                        timestamp = System.currentTimeMillis() - 86400000
                    )
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectMediaTypeAndUpload(mediaType: MediaType, uri: Uri?, bitmap: Bitmap? = null) {
        _activeMediaType.value = mediaType
        _activeMediaUri.value = uri?.toString()
        _activeSourceBitmap.value = bitmap ?: createDefaultSamplePortrait(mediaType)
        _editedResultBitmap.value = null
        _adjustments.value = EditingAdjustments()
        _currentScreen.value = AppScreen.EDITOR
    }

    private fun createDefaultSamplePortrait(mediaType: MediaType): Bitmap {
        val width = 800
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Draw soft studio backdrop
        val paint = android.graphics.Paint()
        paint.color = android.graphics.Color.parseColor("#1E1B2E")
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Draw stylized human figure portrait
        val skinPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#E0A96D")
            isAntiAlias = true
        }
        val hairPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#2D1E12")
            isAntiAlias = true
        }
        val clothPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#9333EA")
            isAntiAlias = true
        }

        // Head
        canvas.drawCircle(400f, 320f, 120f, skinPaint)
        // Hair top
        canvas.drawCircle(400f, 260f, 130f, hairPaint)
        // Body / Shirt
        canvas.drawRoundRect(200f, 440f, 600f, 800f, 60f, 60f, clothPaint)

        return bitmap
    }

    fun selectPreset(preset: BackgroundPreset) {
        if (preset.isPremium && !currentUser.value.isPro) {
            _showPremiumModal.value = true
            return
        }
        _selectedPreset.value = preset
        _customBgBitmap.value = null
    }

    fun setCustomBackground(bitmap: Bitmap) {
        _customBgBitmap.value = bitmap
        _selectedPreset.value = null
    }

    fun updateAdjustments(newAdjustments: EditingAdjustments) {
        _adjustments.value = newAdjustments
    }

    fun processAiBackgroundChange() {
        val user = currentUser.value
        if (!user.isPro && user.remainingCredits <= 0) {
            _showPremiumModal.value = true
            return
        }

        val sourceBitmap = _activeSourceBitmap.value ?: createDefaultSamplePortrait(_activeMediaType.value)

        viewModelScope.launch {
            _isProcessing.value = true
            _processingProgress.value = 0.05f
            _processingStatus.value = "Initializing REWIVO AI engine..."
            _errorMessage.value = null

            try {
                val result = aiEngine.processPhotoBackgroundChange(
                    subjectBitmap = sourceBitmap,
                    backgroundBitmap = _customBgBitmap.value,
                    preset = _selectedPreset.value,
                    adjustments = _adjustments.value,
                    onProgress = { progress, text ->
                        _processingProgress.value = progress
                        _processingStatus.value = text
                    }
                )

                _editedResultBitmap.value = result

                // Save to Room DB project history
                val originalPath = aiEngine.saveBitmapToInternalStorage(sourceBitmap, "orig_${System.currentTimeMillis()}")
                val resultPath = aiEngine.saveBitmapToInternalStorage(result, "res_${System.currentTimeMillis()}")

                val bgName = _customBgBitmap.value?.let { "Custom Upload" }
                    ?: _selectedPreset.value?.title
                    ?: "AI Background"

                projectRepository.saveProject(
                    ProjectEntity(
                        title = if (_activeMediaType.value == MediaType.PHOTO) "Photo BG Change" else "Video BG Change",
                        mediaType = _activeMediaType.value.name,
                        originalPath = originalPath,
                        editedPath = resultPath,
                        backgroundName = bgName,
                        durationSeconds = if (_activeMediaType.value == MediaType.VIDEO) 8 else 0
                    )
                )

                // Deduct 1 credit ONLY on SUCCESSful completion!
                userRepository.useFreeCredit()

                showToast("Background successfully changed!")
            } catch (e: Exception) {
                // DO NOT deduct credit on failure!
                _errorMessage.value = e.localizedMessage ?: "Failed to process AI background change."
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun generateAiBackgroundFromPrompt(prompt: String) {
        if (prompt.isBlank()) return

        val user = currentUser.value
        if (!user.isPro && user.remainingCredits <= 0) {
            _showPremiumModal.value = true
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _processingProgress.value = 0.1f
            _processingStatus.value = "Connecting to Gemini AI..."
            _errorMessage.value = null

            try {
                val generatedBg = aiEngine.generateAiBackgroundFromPrompt(
                    prompt = prompt,
                    onProgress = { progress, status ->
                        _processingProgress.value = progress
                        _processingStatus.value = status
                    }
                )
                _customBgBitmap.value = generatedBg
                _selectedPreset.value = null
                _currentScreen.value = AppScreen.EDITOR
                showToast("Generated AI Background applied!")
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "AI background generation failed."
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            projectRepository.deleteProject(id)
            showToast("Project deleted")
        }
    }

    fun loginWithEmail(email: String, name: String) {
        userRepository.loginWithEmail(email, name)
        _currentScreen.value = AppScreen.HOME
        showToast("Welcome to REWIVO AI!")
    }

    fun loginWithGoogle() {
        userRepository.loginWithGoogle()
        _currentScreen.value = AppScreen.HOME
        showToast("Signed in with Google!")
    }

    fun logout() {
        userRepository.logout()
        _currentScreen.value = AppScreen.ONBOARDING
        showToast("Logged out successfully")
    }

    fun upgradeToPro() {
        userRepository.upgradeToPro()
        _showPremiumModal.value = false
        showToast("Welcome to REWIVO AI PRO! Unlimited access unlocked.")
    }

    fun restorePurchases() {
        userRepository.restorePurchases()
        _showPremiumModal.value = false
        showToast("Purchases restored. PRO subscription active!")
    }

    fun openPaymentCheckout(planName: String = "PRO Monthly", price: Double = 99.0) {
        _selectedCheckoutPlanName.value = planName
        _selectedCheckoutPlanPrice.value = price
        _showPremiumModal.value = false
        _currentScreen.value = AppScreen.PAYMENT_CHECKOUT
    }

    fun openPremiumModal() {
        _showPremiumModal.value = true
    }

    fun dismissPremiumModal() {
        _showPremiumModal.value = false
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun showToast(message: String) {
        _toastNotification.value = message
    }

    fun dismissToast() {
        _toastNotification.value = null
    }
}
