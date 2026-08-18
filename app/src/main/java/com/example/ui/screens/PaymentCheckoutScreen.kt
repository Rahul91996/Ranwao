package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentEntity
import com.example.data.model.User
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
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun PaymentCheckoutScreen(
    mainViewModel: MainViewModel,
    adminViewModel: AdminViewModel,
    selectedPlanName: String = "PRO Monthly",
    selectedPlanPrice: Double = 99.0,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by mainViewModel.currentUser.collectAsState()
    val allPayments by adminViewModel.allPayments.collectAsState()

    // Find if user already submitted a payment request recently
    val userPendingPayment = remember(allPayments, currentUser.id) {
        allPayments.find { it.userId == currentUser.id && it.status == "Pending" }
    }

    var transactionId by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("UPI (GPay/PhonePe/Paytm)") }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val upiId = "rewivo.ai@upi"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("checkout_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REWIVO AI PRO Checkout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (userPendingPayment != null || submitSuccess) {
                // Pending Verification State Screen
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AiPurpleGlow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AiPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = AiCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Payment Request Submitted",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Status: Pending Admin Verification",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AiCyan,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Your payment request for $selectedPlanName (₹${selectedPlanPrice.toInt()}) has been recorded. Once verified by the admin, your REWIVO AI PRO subscription will activate automatically.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Request Detail Summary Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurfaceVariant)
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Plan Selected:", fontSize = 12.sp, color = TextMuted)
                                    Text(selectedPlanName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Amount:", fontSize = 12.sp, color = TextMuted)
                                    Text("₹${selectedPlanPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Txn ID:", fontSize = 12.sp, color = TextMuted)
                                    Text(
                                        userPendingPayment?.transactionId ?: transactionId.ifBlank { "TXN_${System.currentTimeMillis()}" },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AiPurpleGlow
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = AiPurple),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Return to App", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            } else {
                // Payment Form
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(selectedPlanName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Full PRO Features Unlock", fontSize = 12.sp, color = TextMuted)
                            }
                            Text(
                                text = "₹${selectedPlanPrice.toInt()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = AiCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "1. Choose Payment Method", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PaymentMethodChip(
                        title = "UPI / QR",
                        icon = Icons.Default.QrCode,
                        isSelected = selectedMethod.contains("UPI"),
                        onClick = { selectedMethod = "UPI (GPay/PhonePe/Paytm)" },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentMethodChip(
                        title = "Card / Net",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedMethod.contains("Card"),
                        onClick = { selectedMethod = "Credit/Debit Card" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Payment Instructions Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Pay via UPI App (GPay / PhonePe / Paytm / BHIM)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AiPurpleGlow)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBg)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("UPI ID:", fontSize = 10.sp, color = TextMuted)
                                Text(upiId, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }

                            IconButton(onClick = { mainViewModel.showToast("UPI ID Copied!") }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AiCyan)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Send ₹${selectedPlanPrice.toInt()} to the UPI ID above, then copy the Transaction / Reference ID below.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "2. Enter Payment Transaction ID", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = transactionId,
                    onValueChange = { transactionId = it },
                    placeholder = { Text("e.g. TXN9872145300 or UTR Number") },
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = AiPurpleGlow) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AiPurpleGlow,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = AiPurpleGlow,
                        unfocusedLabelColor = TextMuted,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_id_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (transactionId.isBlank()) {
                            mainViewModel.showToast("Please enter transaction ID")
                            return@Button
                        }
                        isSubmitting = true
                        coroutineScope.launch {
                            val userEntity = UserEntity(
                                id = currentUser.id,
                                name = currentUser.name,
                                email = currentUser.email,
                                isPro = currentUser.isPro,
                                freeEditsRemaining = currentUser.remainingCredits
                            )
                            adminViewModel.adminRepository.createPaymentRequest(
                                user = userEntity,
                                plan = selectedPlanName,
                                amount = selectedPlanPrice,
                                transactionId = transactionId,
                                paymentMethod = selectedMethod
                            )
                            isSubmitting = false
                            submitSuccess = true
                            mainViewModel.showToast("Payment submitted for verification!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(AiPurple, AiPink)))
                        .testTag("submit_payment_request_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSubmitting) "Submitting Request..." else "Submit Payment Request",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🔒 Payment requests are verified securely by REWIVO AI administrators.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) AiPurple.copy(alpha = 0.25f) else DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AiPurpleGlow else DarkBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) AiCyan else TextMuted, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) TextPrimary else TextMuted)
        }
    }
}
