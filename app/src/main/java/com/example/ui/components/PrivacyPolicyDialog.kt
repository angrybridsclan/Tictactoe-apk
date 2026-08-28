package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBorder
import com.example.ui.theme.NeonTextMuted
import com.example.ui.theme.NeonWhite

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1F0C3B),
                            NeonDarkSurface,
                            Color(0xFF130626)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            NeonPurpleBorder,
                            NeonCyan,
                            NeonPurple,
                            NeonGold,
                            NeonPurpleBorder
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Policy,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PRIVACY POLICY",
                            color = NeonWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_privacy")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Privacy Policy",
                            tint = NeonTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Developer Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x333F1070)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Lead Developer",
                                    color = NeonGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Mahinur Rahman Saif",
                                color = NeonWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "mahinurrahmansaif@gmail.com",
                                    color = NeonCyanLight,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // App Package & Version details
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x44000000), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "App: Tic Tac Toe (Neon Cyber)",
                                        color = NeonWhite,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Package: com.aistudio.tictactoe.neon",
                                        color = NeonCyanLight,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Version: 1.0.0 (Build 1)",
                                        color = NeonGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Section 1: Overview
                    PolicySection(
                        title = "1. Overview & Information Collection",
                        body = "Tic Tac Toe Neon Cyber is dedicated to protecting user privacy. This application does not collect, sell, or rent any personal identifying information (PII). All user gameplay data, high scores, coin balances, and unlocked themes are stored locally on your device using Android Shared Preferences."
                    )

                    // Section 2: Advertising (Unity Ads)
                    PolicySection(
                        title = "2. Unity Ads Advertising",
                        body = "The app integrates the Unity Ads SDK (Game ID: 5857887) to serve Banner, Interstitial, Native, and Rewarded Video ads. Unity Ads may process non-personalized device identifiers and diagnostic metrics in accordance with Unity's Advertising & Privacy Policies."
                    )

                    // Section 3: Notification Permissions
                    PolicySection(
                        title = "3. Push & Local Notifications",
                        body = "The app may request the POST_NOTIFICATIONS permission to send notifications about daily rewards, game invites, and coin bonuses. You may grant or revoke this permission at any time through your Android System Settings."
                    )

                    // Section 4: Security
                    PolicySection(
                        title = "4. Data Security",
                        body = "We use standard Android security practices to keep local game state safe. Since no personal user accounts or payment data are collected, your gameplay experience remains confidential and secure."
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Agree / Close Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("btn_privacy_understand")
                ) {
                    Text(
                        text = "I UNDERSTAND & AGREE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PolicySection(
    title: String,
    body: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = NeonGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = body,
            color = NeonTextMuted,
            fontSize = 11.5.sp,
            lineHeight = 16.sp
        )
    }
}
