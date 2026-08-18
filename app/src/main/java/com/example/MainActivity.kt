package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.RewivoBottomBar
import com.example.ui.components.ToastBanner
import com.example.ui.screens.AiGeneratorScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingAuthScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.RewivoTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

import com.example.ui.screens.PaymentCheckoutScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminLoginScreen
import com.example.ui.viewmodel.AdminViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RewivoTheme {
                RewivoMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewivoMainApp() {
    val viewModel: MainViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val showPremiumModal by viewModel.showPremiumModal.collectAsState()
    val toastNotification by viewModel.toastNotification.collectAsState()
    val isAdminLoggedIn by adminViewModel.isAdminLoggedIn.collectAsState()
    val selectedPlanName by viewModel.selectedCheckoutPlanName.collectAsState()
    val selectedPlanPrice by viewModel.selectedCheckoutPlanPrice.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showBottomBar = currentUser.isLoggedIn && (
        currentScreen == AppScreen.HOME ||
        currentScreen == AppScreen.CREATE ||
        currentScreen == AppScreen.PROJECTS ||
        currentScreen == AppScreen.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBg,
        bottomBar = {
            if (showBottomBar) {
                RewivoBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        if (screen == AppScreen.CREATE) {
                            viewModel.selectMediaTypeAndUpload(com.example.data.model.MediaType.PHOTO, null)
                        } else {
                            viewModel.navigateTo(screen)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        if (!currentUser.isLoggedIn) {
            OnboardingAuthScreen(viewModel = viewModel, modifier = screenModifier)
        } else {
            when (currentScreen) {
                AppScreen.ONBOARDING -> OnboardingAuthScreen(viewModel = viewModel, modifier = screenModifier)
                AppScreen.HOME -> HomeScreen(viewModel = viewModel, modifier = screenModifier)
                AppScreen.CREATE, AppScreen.EDITOR -> EditorScreen(viewModel = viewModel, modifier = screenModifier)
                AppScreen.AI_GENERATOR -> AiGeneratorScreen(viewModel = viewModel, modifier = screenModifier)
                AppScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel, modifier = screenModifier)
                AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel, modifier = screenModifier)
                AppScreen.PREMIUM -> PremiumScreen(viewModel = viewModel, onDismiss = { viewModel.navigateTo(AppScreen.HOME) }, modifier = screenModifier)
                AppScreen.PAYMENT_CHECKOUT -> PaymentCheckoutScreen(
                    mainViewModel = viewModel,
                    adminViewModel = adminViewModel,
                    selectedPlanName = selectedPlanName,
                    selectedPlanPrice = selectedPlanPrice,
                    onBack = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = screenModifier
                )
                AppScreen.ADMIN -> {
                    if (isAdminLoggedIn) {
                        AdminDashboardScreen(
                            adminViewModel = adminViewModel,
                            onExitAdmin = { viewModel.navigateTo(AppScreen.HOME) },
                            modifier = screenModifier
                        )
                    } else {
                        AdminLoginScreen(
                            adminViewModel = adminViewModel,
                            onBackToUserApp = { viewModel.navigateTo(AppScreen.HOME) },
                            modifier = screenModifier
                        )
                    }
                }
            }
        }

        ToastBanner(
            message = toastNotification,
            onDismiss = { viewModel.dismissToast() }
        )

        if (showPremiumModal) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissPremiumModal() },
                sheetState = sheetState,
                containerColor = DarkBg
            ) {
                PremiumScreen(
                    viewModel = viewModel,
                    onDismiss = { viewModel.dismissPremiumModal() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
