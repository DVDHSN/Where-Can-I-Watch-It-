package com.example.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.CountryPresets
import com.example.ui.components.ProviderLogo
import com.example.ui.components.applePressEffect
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleLabelTertiary
import com.example.ui.theme.ApplePurple
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleSeparator
import com.example.ui.theme.AppleTertiaryBackground

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    onShowNotification: ((ImageVector, String, Color) -> Unit)? = null
) {
    val countryCode by viewModel.countryCode.collectAsStateWithLifecycle()
    val subscribedProviderIds by viewModel.subscribedProviderIds.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val cacheMessage by viewModel.cacheClearedMessage.collectAsStateWithLifecycle()

    var apiKeyInput by remember(customApiKey) { mutableStateOf(customApiKey) }
    val currentCountry = CountryPresets.findByCode(countryCode)

    LaunchedEffect(cacheMessage) {
        if (cacheMessage != null) {
            onShowNotification?.invoke(Icons.Default.Delete, cacheMessage ?: "Cache cleared", AppleGreen)
            kotlinx.coroutines.delay(2500)
            viewModel.dismissCacheMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppleBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .testTag("settings_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Apple Large Title Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displayMedium,
            color = AppleLabelPrimary
        )
        Text(
            text = "Streaming catalogs & preferences",
            style = MaterialTheme.typography.bodySmall,
            color = AppleLabelSecondary,
            letterSpacing = (-0.1).sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 1: Region / Country Selection
        AppleGroupHeader(title = "REGION & CATALOG")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppleSecondaryBackground),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, AppleBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppleIconSquircle(icon = Icons.Default.Public, tint = Color.White, bg = AppleBlue)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Active Country",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AppleLabelPrimary
                        )
                        Text(
                            text = "${currentCountry.flagEmoji} ${currentCountry.name} (${currentCountry.code})",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleLabelSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Country selector pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountryPresets.ALL_COUNTRIES.take(3).forEach { country ->
                        val isSelected = country.code.equals(countryCode, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AppleGold else AppleTertiaryBackground)
                                .border(
                                    0.6.dp,
                                    if (isSelected) AppleGold else AppleBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .applePressEffect(scaleDown = 0.92f) {
                                    viewModel.setCountry(country.code)
                                    onShowNotification?.invoke(Icons.Default.Public, "Switched region to ${country.name}", AppleBlue)
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${country.flagEmoji} ${country.code}",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF141000) else AppleLabelPrimary,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountryPresets.ALL_COUNTRIES.drop(3).forEach { country ->
                        val isSelected = country.code.equals(countryCode, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AppleGold else AppleTertiaryBackground)
                                .border(
                                    0.6.dp,
                                    if (isSelected) AppleGold else AppleBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .applePressEffect(scaleDown = 0.92f) {
                                    viewModel.setCountry(country.code)
                                    onShowNotification?.invoke(Icons.Default.Public, "Switched region to ${country.name}", AppleBlue)
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${country.flagEmoji} ${country.code}",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF141000) else AppleLabelPrimary,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 2: Subscriptions Checklist
        AppleGroupHeader(title = "MY STREAMING SUBSCRIPTIONS")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppleSecondaryBackground),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, AppleBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Verdicts are calculated strictly against your selected plans:",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                currentCountry.popularProviders.forEachIndexed { index, provider ->
                    val isChecked = subscribedProviderIds.contains(provider.id)

                    val checkScale by animateFloatAsState(
                        targetValue = if (isChecked) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "check_scale"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isChecked) AppleTertiaryBackground else Color.Transparent)
                            .applePressEffect(scaleDown = 0.96f) {
                                viewModel.toggleSubscription(provider.id)
                                if (!isChecked) {
                                    onShowNotification?.invoke(Icons.Default.CheckCircle, "Added ${provider.name} to active plans", AppleGreen)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProviderLogo(logoUrl = provider.logoUrl, providerName = provider.name, size = 28.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = provider.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = AppleLabelPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        // Apple Checkmark Pill
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isChecked) AppleGreen else Color(0x33FFFFFF))
                                .border(
                                    0.5.dp,
                                    if (isChecked) AppleGreen else Color(0x44FFFFFF),
                                    CircleShape
                                )
                                .scale(checkScale),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    if (index < currentCountry.popularProviders.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 48.dp)
                                .height(0.5.dp)
                                .background(AppleSeparator)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 3: Notification Preferences
        AppleGroupHeader(title = "NOTIFICATIONS")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppleSecondaryBackground),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, AppleBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    AppleIconSquircle(icon = Icons.Default.Notifications, tint = Color.White, bg = ApplePurple)
                    Column {
                        Text(
                            text = "Availability Alerts",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AppleLabelPrimary
                        )
                        Text(
                            text = "Notify when a saved title is included in subscriptions",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleLabelSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        viewModel.setNotificationsEnabled(it)
                        onShowNotification?.invoke(Icons.Default.Notifications, if (it) "Notifications enabled" else "Notifications disabled", ApplePurple)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppleGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = AppleTertiaryBackground
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 4: TMDB API Configuration
        AppleGroupHeader(title = "TMDB API CONFIGURATION")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppleSecondaryBackground),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, AppleBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppleIconSquircle(icon = Icons.Default.Key, tint = Color(0xFF141000), bg = AppleGold)
                    Column(modifier = Modifier.weight(1f)) {
                        if (viewModel.isBuildConfigKeyAvailable) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AppleGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Connected via AI Studio Secrets",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = AppleGreen
                                )
                            }
                        } else {
                            Text(
                                text = "Personal TMDB Key / Token",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = AppleLabelPrimary
                            )
                        }
                        Text(
                            text = "Required for movie search and streaming availability queries",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleLabelSecondary
                        )
                    }
                }

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Paste TMDB API Key or Bearer Token", fontSize = 12.5.sp, color = AppleLabelTertiary) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppleTertiaryBackground,
                        unfocusedContainerColor = AppleTertiaryBackground,
                        focusedBorderColor = AppleBorder,
                        unfocusedBorderColor = AppleBorder,
                        focusedTextColor = AppleLabelPrimary,
                        unfocusedTextColor = AppleLabelPrimary,
                        cursorColor = AppleGold
                    )
                )

                Button(
                    onClick = {
                        viewModel.saveCustomApiKey(apiKeyInput)
                        onShowNotification?.invoke(Icons.Default.CheckCircle, "API key configuration saved", AppleGold)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .applePressEffect(scaleDown = 0.96f) {
                            viewModel.saveCustomApiKey(apiKeyInput)
                            onShowNotification?.invoke(Icons.Default.CheckCircle, "API key saved", AppleGold)
                        },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = AppleGold)
                ) {
                    Text("Save Key", color = Color(0xFF141000), fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 5: Cache Management & Attribution
        AppleGroupHeader(title = "DATA & STORAGE")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppleSecondaryBackground),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, AppleBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .applePressEffect(scaleDown = 0.96f) { viewModel.clearCache() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppleIconSquircle(icon = Icons.Default.Delete, tint = Color.White, bg = AppleRed)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Clear Offline Cache",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AppleLabelPrimary
                        )
                        Text(
                            text = "Purge cached titles and availability data",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppleLabelSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AppleLabelTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(AppleSeparator)
                )

                Text(
                    text = "This product uses the TMDB API but is not endorsed or certified by TMDB. Regional streaming provider metadata is powered by JustWatch.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary,
                    lineHeight = 17.sp,
                    fontSize = 11.5.sp
                )

                Text(
                    text = "Where Can I Watch It? • Version 1.0 (Apple Edition)",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppleLabelTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun AppleGroupHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = AppleLabelTertiary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
    )
}

@Composable
private fun AppleIconSquircle(icon: ImageVector, tint: Color, bg: Color) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(17.dp)
        )
    }
}
