package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.CountryPresets
import com.example.domain.model.ProviderPreset
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleLabelPrimary
import com.example.ui.theme.AppleLabelSecondary
import com.example.ui.theme.AppleSecondaryBackground
import com.example.ui.theme.AppleTertiaryBackground

/**
 * Apple iOS Welcome Onboarding Dialog.
 * Two-step progressive setup with tactile spring animations and minimalist typography.
 */
@Composable
fun OnboardingDialog(
    initialCountryCode: String,
    initialSelectedProviders: Set<Int>,
    onComplete: (countryCode: String, selectedProviders: Set<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedCountryCode by remember { mutableStateOf(initialCountryCode) }
    var selectedProviderIds by remember { mutableStateOf(initialSelectedProviders) }

    val currentCountry = remember(selectedCountryCode) {
        CountryPresets.findByCode(selectedCountryCode)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 660.dp)
                .testTag("onboarding_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161618)),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, AppleBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon Emblem with Apple Glow
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AppleGold.copy(alpha = 0.14f))
                        .border(1.dp, AppleGold.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentStep == 1) Icons.Default.Public else Icons.Default.LiveTv,
                        contentDescription = null,
                        tint = AppleGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (currentStep == 1) "Select Your Region" else "Choose Your Subscriptions",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = AppleLabelPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (currentStep == 1)
                        "Streaming rights and catalogs vary by country. Choose your current location."
                    else
                        "We will prioritize movies and shows included with these active services.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleLabelSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Apple Step Progress Indicator (Pills)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) { index ->
                        val isCurrent = (index + 1) == currentStep
                        val pillWidth by animateDpAsState(
                            targetValue = if (isCurrent) 24.dp else 8.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "step_pill_width"
                        )
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(pillWidth)
                                .clip(CircleShape)
                                .background(if (isCurrent) AppleGold else Color(0x33FFFFFF))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Step Content
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn(spring()) togetherWith fadeOut(spring()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    label = "onboarding_step_content"
                ) { step ->
                    if (step == 1) {
                        StepCountrySelection(
                            selectedCode = selectedCountryCode,
                            onSelect = { code ->
                                selectedCountryCode = code
                                val country = CountryPresets.findByCode(code)
                                selectedProviderIds = country.popularProviders
                                    .filter { it.isDefaultSelected }
                                    .map { it.id }
                                    .toSet()
                            }
                        )
                    } else {
                        StepProviderSelection(
                            providers = currentCountry.popularProviders,
                            selectedIds = selectedProviderIds,
                            onToggle = { id ->
                                selectedProviderIds = if (selectedProviderIds.contains(id)) {
                                    selectedProviderIds - id
                                } else {
                                    selectedProviderIds + id
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep == 2) {
                        Button(
                            onClick = { currentStep = 1 },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = AppleSecondaryBackground),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .border(0.6.dp, AppleBorder, CircleShape)
                                .applePressEffect(scaleDown = 0.94f) { currentStep = 1 }
                        ) {
                            Text("Back", color = AppleLabelPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep == 1) {
                                currentStep = 2
                            } else {
                                onComplete(selectedCountryCode, selectedProviderIds)
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = AppleGold),
                        modifier = Modifier
                            .weight(if (currentStep == 2) 1.4f else 1f)
                            .height(48.dp)
                            .applePressEffect(scaleDown = 0.94f) {
                                if (currentStep == 1) {
                                    currentStep = 2
                                } else {
                                    onComplete(selectedCountryCode, selectedProviderIds)
                                }
                            }
                            .testTag("onboarding_continue_button")
                    ) {
                        Text(
                            text = if (currentStep == 1) "Continue" else "Get Started",
                            color = Color(0xFF141000),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCountrySelection(
    selectedCode: String,
    onSelect: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(CountryPresets.ALL_COUNTRIES) { country ->
            val isSelected = country.code.equals(selectedCode, ignoreCase = true)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) AppleTertiaryBackground else AppleSecondaryBackground)
                    .border(
                        0.6.dp,
                        if (isSelected) AppleGold else AppleBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .applePressEffect(scaleDown = 0.96f) { onSelect(country.code) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = country.flagEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = country.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AppleLabelPrimary
                    )
                    Text(
                        text = "${country.popularProviders.size} streaming services supported",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppleLabelSecondary
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(AppleGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF141000),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepProviderSelection(
    providers: List<ProviderPreset>,
    selectedIds: Set<Int>,
    onToggle: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(providers) { provider ->
            val isSelected = selectedIds.contains(provider.id)
            val checkScale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "onboard_check_scale"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) AppleTertiaryBackground else AppleSecondaryBackground)
                    .border(
                        0.6.dp,
                        if (isSelected) AppleGreen else AppleBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .applePressEffect(scaleDown = 0.96f) { onToggle(provider.id) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProviderLogo(logoUrl = provider.logoUrl, providerName = provider.name, size = 30.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = provider.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = AppleLabelPrimary,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) AppleGreen else Color(0x33FFFFFF))
                        .border(
                            0.5.dp,
                            if (isSelected) AppleGreen else Color(0x44FFFFFF),
                            CircleShape
                        )
                        .scale(checkScale),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
