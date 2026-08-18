package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminLogEntity
import com.example.data.model.AiProcessingEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiPink
import com.example.ui.theme.AiPurple
import com.example.ui.theme.AiPurpleGlow
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AdminTab
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTab by adminViewModel.currentAdminTab.collectAsState()
    val allUsers by adminViewModel.allUsers.collectAsState()
    val allPayments by adminViewModel.allPayments.collectAsState()
    val allAiProcessing by adminViewModel.allAiProcessing.collectAsState()
    val allLogs by adminViewModel.allAdminLogs.collectAsState()
    val allPlans by adminViewModel.allPlans.collectAsState()
    val appSettings by adminViewModel.appSettings.collectAsState()
    val adminToast by adminViewModel.adminToast.collectAsState()

    val pendingCount = remember(allPayments) { allPayments.count { it.status == "Pending" } }
    val approvedRevenue = remember(allPayments) { allPayments.filter { it.status == "Approved" }.sumOf { it.amount } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // TOP ADMIN HEADER
        val adminEmail by adminViewModel.adminEmail.collectAsState()
        val adminRole by adminViewModel.adminRole.collectAsState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(AiPurple, AiPink))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("REWIVO AI", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AiPurple)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(adminRole ?: "SUPER_ADMIN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Text(
                        text = if (adminEmail.isNotBlank()) adminEmail else "piyushkumar44397@gmail.com",
                        fontSize = 11.sp,
                        color = AiCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FF5252))
                        .clickable {
                            adminViewModel.logoutAdmin()
                            onExitAdmin()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Logout", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    }
                }
            }
        }

        // SCROLLABLE NAVIGATION TABS
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant)
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AdminTab.values()) { tab ->
                val isSelected = currentTab == tab
                val badgeCount = if (tab == AdminTab.PAYMENTS && pendingCount > 0) pendingCount else 0

                AdminNavTabChip(
                    tab = tab,
                    isSelected = isSelected,
                    badgeCount = badgeCount,
                    onClick = { adminViewModel.setAdminTab(tab) }
                )
            }
        }

        // TAB CONTENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (currentTab) {
                AdminTab.DASHBOARD -> DashboardTabContent(adminViewModel, allUsers, allPayments, allAiProcessing)
                AdminTab.PAYMENTS -> PaymentsTabContent(adminViewModel, allPayments)
                AdminTab.USERS -> UsersTabContent(adminViewModel, allUsers)
                AdminTab.AI_PROCESSING -> AiProcessingTabContent(allAiProcessing)
                AdminTab.PROJECTS -> ProjectsTabContent(adminViewModel)
                AdminTab.SUBSCRIPTIONS -> SubscriptionsTabContent(adminViewModel, allPlans)
                AdminTab.REVENUE -> RevenueTabContent(allPayments, approvedRevenue)
                AdminTab.LOGS -> LogsTabContent(allLogs)
                AdminTab.NOTIFICATIONS -> NotificationsTabContent(adminViewModel)
                AdminTab.SETTINGS -> SettingsTabContent(adminViewModel, appSettings ?: AppSettingsEntity())
            }
        }
    }
}

@Composable
private fun AdminNavTabChip(
    tab: AdminTab,
    isSelected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit
) {
    val (label, icon) = when (tab) {
        AdminTab.DASHBOARD -> "Dashboard" to Icons.Default.Dashboard
        AdminTab.PAYMENTS -> "Payments" to Icons.Default.Payment
        AdminTab.USERS -> "Users" to Icons.Default.People
        AdminTab.AI_PROCESSING -> "AI Jobs" to Icons.Default.AutoAwesome
        AdminTab.PROJECTS -> "Projects" to Icons.Default.Movie
        AdminTab.SUBSCRIPTIONS -> "Plans" to Icons.Default.Subscriptions
        AdminTab.REVENUE -> "Revenue" to Icons.Default.AttachMoney
        AdminTab.LOGS -> "Activity Logs" to Icons.Default.History
        AdminTab.NOTIFICATIONS -> "Broadcast" to Icons.Default.Notifications
        AdminTab.SETTINGS -> "App Settings" to Icons.Default.Settings
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AiPurple else DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AiPurpleGlow else DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else TextMuted, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else TextMuted)

            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(AiPink),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$badgeCount", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ---------------- DASHBOARD TAB ----------------
@Composable
private fun DashboardTabContent(
    adminViewModel: AdminViewModel,
    users: List<UserEntity>,
    payments: List<PaymentEntity>,
    aiLogs: List<AiProcessingEntity>
) {
    val totalUsers = users.size
    val activeUsers = users.count { !it.isSuspended }
    val premiumUsers = users.count { it.isPro }
    val freeUsers = totalUsers - premiumUsers
    val pendingPayments = payments.filter { it.status == "Pending" }
    val approvedRev = payments.filter { it.status == "Approved" }.sumOf { it.amount }
    val totalAiEdits = aiLogs.size
    val totalVideoEdits = aiLogs.count { it.mediaType == "VIDEO" }
    val failedJobs = aiLogs.count { it.status == "Failed" }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text("Real-Time System Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        // Grid of Real-Time Metrics
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(title = "Total Users", value = "$totalUsers", icon = Icons.Default.People, tint = AiPurpleGlow, modifier = Modifier.weight(1f))
            StatCard(title = "Premium Users", value = "$premiumUsers", icon = Icons.Default.Star, tint = AiCyan, modifier = Modifier.weight(1f))
            StatCard(title = "Pending Payments", value = "${pendingPayments.size}", icon = Icons.Default.Payment, tint = AiPink, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(title = "Approved Revenue", value = "₹${approvedRev.toInt()}", icon = Icons.Default.AttachMoney, tint = Color(0xFF10B981), modifier = Modifier.weight(1f))
            StatCard(title = "Total AI Edits", value = "$totalAiEdits", icon = Icons.Default.AutoAwesome, tint = AiPurpleGlow, modifier = Modifier.weight(1f))
            StatCard(title = "Failed Processing", value = "$failedJobs", icon = Icons.Default.Warning, tint = AiPink, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pending Payment Requests Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pending Payment Approvals (${pendingPayments.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            if (pendingPayments.isNotEmpty()) {
                TextButton(onClick = { adminViewModel.setAdminTab(AdminTab.PAYMENTS) }) {
                    Text("View All", fontSize = 12.sp, color = AiCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pendingPayments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("✅ No pending payment requests right now.", fontSize = 13.sp, color = TextMuted)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pendingPayments.take(3).forEach { payment ->
                    PaymentRequestCard(payment = payment, onApprove = { adminViewModel.approvePayment(payment.paymentId) }, onReject = { adminViewModel.rejectPayment(payment.paymentId, null) })
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(title, fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
private fun PaymentRequestCard(
    payment: PaymentEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateStr = remember(payment.createdAt) { dateFormatter.format(Date(payment.createdAt)) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(payment.userName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("User ID: ${payment.userId} | ${payment.userEmail}", fontSize = 11.sp, color = TextMuted)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AiPink.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(payment.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AiPink)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Plan: ${payment.plan} (₹${payment.amount.toInt()})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AiCyan)
                Text("Txn: ${payment.transactionId}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("APPROVE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = AiPink),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("REJECT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ---------------- PAYMENTS TAB ----------------
@Composable
private fun PaymentsTabContent(
    adminViewModel: AdminViewModel,
    payments: List<PaymentEntity>
) {
    var filterStatus by remember { mutableStateOf("ALL") }
    var selectedForReject by remember { mutableStateOf<PaymentEntity?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    val filtered = remember(payments, filterStatus) {
        if (filterStatus == "ALL") payments else payments.filter { it.status.equals(filterStatus, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Payment Requests System", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ALL", "Pending", "Approved", "Rejected").forEach { status ->
                val isSel = filterStatus == status
                Surface(
                    onClick = { filterStatus = status },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) AiPurple else DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AiPurpleGlow else DarkBorder)
                ) {
                    Text(
                        status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) Color.White else TextMuted,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filtered) { payment ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(payment.userName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("ID: ${payment.userId} • ${payment.userEmail}", fontSize = 11.sp, color = TextMuted)
                            }

                            val badgeColor = when (payment.status) {
                                "Approved" -> Color(0xFF10B981)
                                "Rejected" -> AiPink
                                else -> AiPurpleGlow
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(payment.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Plan: ${payment.plan} — ₹${payment.amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AiCyan)
                        Text("Transaction ID: ${payment.transactionId}", fontSize = 11.sp, color = TextSecondary)
                        Text("Method: ${payment.paymentMethod}", fontSize = 11.sp, color = TextMuted)

                        if (!payment.rejectionReason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Reason: ${payment.rejectionReason}", fontSize = 11.sp, color = AiPink)
                        }

                        if (payment.status == "Pending") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { adminViewModel.approvePayment(payment.paymentId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("✅ APPROVE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = { selectedForReject = payment },
                                    colors = ButtonDefaults.buttonColors(containerColor = AiPink),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("❌ REJECT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Reject Reason Modal
    selectedForReject?.let { target ->
        AlertDialog(
            onDismissRequest = { selectedForReject = null },
            title = { Text("Reject Payment Request") },
            text = {
                Column {
                    Text("Enter optional rejection reason for ${target.userName}:", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("e.g. Transaction ID not found") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        adminViewModel.rejectPayment(target.paymentId, rejectReason)
                        selectedForReject = null
                        rejectReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AiPink)
                ) {
                    Text("Confirm Rejection", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedForReject = null }) { Text("Cancel", color = TextMuted) }
            },
            containerColor = DarkSurface
        )
    }
}

// ---------------- USERS TAB ----------------
@Composable
private fun UsersTabContent(
    adminViewModel: AdminViewModel,
    users: List<UserEntity>
) {
    val searchQuery by adminViewModel.userSearchQuery.collectAsState()
    val filtered = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users else users.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true) ||
            it.id.contains(searchQuery, ignoreCase = true)
        }
    }

    var selectedUserForEdit by remember { mutableStateOf<UserEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("User Management & Controls", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { adminViewModel.setUserSearchQuery(it) },
            placeholder = { Text("Search users by Name, Email, ID...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AiPurpleGlow) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered) { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(user.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                if (user.isPro) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AiPurple)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("PRO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                            Text("ID: ${user.id} | ${user.email}", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Free Edits: ${user.freeEditsRemaining}/3 | Total Edits: ${user.totalAiEdits}", fontSize = 11.sp, color = AiCyan)
                        }

                        IconButton(onClick = { selectedUserForEdit = user }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit User", tint = AiPurpleGlow)
                        }
                    }
                }
            }
        }
    }

    // User Control Dialog Modal
    selectedUserForEdit?.let { targetUser ->
        AlertDialog(
            onDismissRequest = { selectedUserForEdit = null },
            title = { Text("User Control: ${targetUser.name}") },
            text = {
                Column {
                    Text("User ID: ${targetUser.id}", fontSize = 12.sp, color = TextMuted)
                    Text("Status: ${if (targetUser.isPro) "PRO User (${targetUser.subscriptionStatus})" else "Free Tier"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AiCyan)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!targetUser.isPro) {
                        Button(
                            onClick = { adminViewModel.grantPremiumManually(targetUser.id, "PRO Monthly", 30) },
                            colors = ButtonDefaults.buttonColors(containerColor = AiPurple),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Grant PRO Monthly (30 Days)", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { adminViewModel.removePremiumManually(targetUser.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = AiPink),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove PRO Status", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { adminViewModel.resetFreeEdits(targetUser.id, 3) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset Free Edits to 3/3", color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { adminViewModel.toggleUserSuspension(targetUser.id, targetUser.isSuspended) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (targetUser.isSuspended) "Reactivate Account" else "Suspend Account", color = AiPink)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedUserForEdit = null }) { Text("Close", color = TextMuted) }
            },
            containerColor = DarkSurface
        )
    }
}

// ---------------- AI PROCESSING TAB ----------------
@Composable
private fun AiProcessingTabContent(logs: List<AiProcessingEntity>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("AI Background Processing Monitor", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(logs) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Job: ${log.processingId} (${log.processingType})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("User: ${log.userName} (${log.userId}) | Type: ${log.mediaType}", fontSize = 11.sp, color = TextMuted)
                            Text("Duration: ${log.durationMs}ms", fontSize = 11.sp, color = AiCyan)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (log.status == "Completed") Color(0xFF10B981).copy(alpha = 0.2f) else AiPink.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(log.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (log.status == "Completed") Color(0xFF10B981) else AiPink)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- PROJECTS TAB ----------------
@Composable
private fun ProjectsTabContent(adminViewModel: AdminViewModel) {
    Text("User Projects Gallery Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    Spacer(modifier = Modifier.height(12.dp))
    Text("All projects generated in REWIVO AI are securely stored with full project history.", fontSize = 12.sp, color = TextMuted)
}

// ---------------- SUBSCRIPTIONS / PLANS TAB ----------------
@Composable
private fun SubscriptionsTabContent(adminViewModel: AdminViewModel, plans: List<SubscriptionPlanEntity>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Manage Subscription Plans & Pricing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(plans) { plan ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(plan.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("₹${plan.price.toInt()} / ${plan.interval}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AiCyan)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Benefits:\n${plan.benefits}", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

// ---------------- REVENUE TAB ----------------
@Composable
private fun RevenueTabContent(payments: List<PaymentEntity>, approvedRev: Double) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Revenue & Transaction Ledger", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Total Verified Revenue", fontSize = 12.sp, color = TextMuted)
                Text("₹${approvedRev.toInt()}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
            }
        }
    }
}

// ---------------- LOGS TAB ----------------
@Composable
private fun LogsTabContent(logs: List<AdminLogEntity>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Admin Security & Action Logs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(logs) { log ->
                val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
                val dateStr = remember(log.createdAt) { dateFormatter.format(Date(log.createdAt)) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(log.action, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AiPurpleGlow)
                            Text(dateStr, fontSize = 10.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(log.details, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

// ---------------- NOTIFICATIONS TAB ----------------
@Composable
private fun NotificationsTabContent(adminViewModel: AdminViewModel) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("ALL") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Broadcast System Notification", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notification Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Body") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            adminViewModel.sendNotification(target, title, message)
                            title = ""
                            message = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AiPurple),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Broadcast", color = Color.White)
                }
            }
        }
    }
}

// ---------------- SETTINGS TAB ----------------
@Composable
private fun SettingsTabContent(adminViewModel: AdminViewModel, settings: AppSettingsEntity) {
    var appName by remember { mutableStateOf(settings.appName) }
    var supportEmail by remember { mutableStateOf(settings.supportEmail) }
    var maintenanceMode by remember { mutableStateOf(settings.maintenanceMode) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Global App Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("App Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = supportEmail,
                    onValueChange = { supportEmail = it },
                    label = { Text("Support Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Maintenance Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Temporarily pause user AI background processing", fontSize = 11.sp, color = TextMuted)
                    }

                    Switch(
                        checked = maintenanceMode,
                        onCheckedChange = { maintenanceMode = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = AiPink)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        adminViewModel.updateSettings(
                            settings.copy(
                                appName = appName,
                                supportEmail = supportEmail,
                                maintenanceMode = maintenanceMode
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AiPurple),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save App Settings", color = Color.White)
                }
            }
        }
    }
}
