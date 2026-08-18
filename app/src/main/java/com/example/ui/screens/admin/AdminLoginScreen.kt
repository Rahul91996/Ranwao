package com.example.ui.screens.admin

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GMobiledata
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiPink
import com.example.ui.theme.AiPurple
import com.example.ui.theme.AiPurpleGlow
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun AdminLoginScreen(
    adminViewModel: AdminViewModel,
    onBackToUserApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("piyushkumar44397@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var rememberSession by remember { mutableStateOf(true) }

    val accessDeniedError by adminViewModel.accessDeniedError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackToUserApp, modifier = Modifier.testTag("admin_back_to_app_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Shield Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AiPurple, AiPink))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "REWIVO AI Admin Portal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Text(
                text = "Super Admin Authentication & Security Console",
                fontSize = 12.sp,
                color = AiPurpleGlow,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SUPER ADMIN AUTHORIZED CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Admin Identity Verification",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AiPurple.copy(alpha = 0.2f))
                                .border(1.dp, AiPurple, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SUPER_ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AiCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // GOOGLE SIGN-IN BUTTON
                    Button(
                        onClick = {
                            adminViewModel.loginWithGoogle(email)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F2E)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_google_signin_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF4285F4))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Sign in with Google", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(DarkBorder))
                        Text(
                            text = "  OR EMAIL AUTH  ",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(DarkBorder))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Admin Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Admin Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AiPurpleGlow) },
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
                            .testTag("admin_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AiPurpleGlow) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                            .testTag("admin_password_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // QUICK EMAIL DEMO TOGGLES
                    Text("Select Account to Test Authorization Rule:", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (email == "piyushkumar44397@gmail.com") AiPurple.copy(alpha = 0.3f) else DarkBg)
                                .border(1.dp, if (email == "piyushkumar44397@gmail.com") AiPurple else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { email = "piyushkumar44397@gmail.com" }
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Super Admin (Valid)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AiCyan)
                                Text("piyushkumar44397@gmail.com", fontSize = 9.sp, color = TextSecondary, maxLines = 1)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (email != "piyushkumar44397@gmail.com") Color(0x33FF5252) else DarkBg)
                                .border(1.dp, if (email != "piyushkumar44397@gmail.com") Color(0xFFFF5252) else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { email = "unauthorized_user@gmail.com" }
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Unauthorized User", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                                Text("unauthorized_user@gmail.com", fontSize = 9.sp, color = TextSecondary, maxLines = 1)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { adminViewModel.loginAdmin(email, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(AiPurple, AiPink)))
                            .testTag("admin_login_submit_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Login to Admin Panel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Protected by REWIVO AI Server Verification\nAuthorized Super Admin: piyushkumar44397@gmail.com",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }

        // ACCESS DENIED MODAL / DIALOG
        accessDeniedError?.let { errorMessage ->
            AlertDialog(
                onDismissRequest = { adminViewModel.clearAccessDeniedError() },
                containerColor = DarkSurface,
                titleContentColor = Color(0xFFFF5252),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = {
                    Text(
                        text = "Access Denied",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFFF5252)
                    )
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x22FF5252))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Only authorized email piyushkumar44397@gmail.com can access the REWIVO AI Admin Panel.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            adminViewModel.clearAccessDeniedError()
                            email = "piyushkumar44397@gmail.com"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AiPurple)
                    ) {
                        Text("Switch to Authorized Admin")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { adminViewModel.clearAccessDeniedError() }) {
                        Text("Close", color = TextMuted)
                    }
                }
            )
        }
    }
}
