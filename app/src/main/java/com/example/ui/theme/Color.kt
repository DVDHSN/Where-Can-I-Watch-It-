package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Apple Human Interface System Color Palette
// ==========================================

// Apple Pure Blacks & System Backgrounds (iOS Dark Mode)
val AppleBackground = Color(0xFF000000) // True OLED Black
val AppleSecondaryBackground = Color(0xFF1C1C1E) // iOS System Gray 6 Dark / Card Surface
val AppleTertiaryBackground = Color(0xFF2C2C2E) // iOS System Gray 5 Dark / Grouped Surface
val AppleQuaternaryBackground = Color(0xFF3A3A3C) // iOS System Gray 4 Dark / Elevated Component

// Apple Hairline Specular Borders & Separators
val AppleBorder = Color(0x24FFFFFF) // 14% White Hairline Stroke
val AppleBorderLight = Color(0x38FFFFFF) // 22% White Highlight
val AppleSeparator = Color(0x2E545458) // iOS Grouped List Separator

// Apple System Accents
val AppleGold = Color(0xFFFFD60A) // iOS System Yellow / Cinema Gold
val AppleBlue = Color(0xFF0A84FF) // iOS System Blue
val AppleGreen = Color(0xFF30D158) // iOS System Green
val AppleOrange = Color(0xFFFF9F0A) // iOS System Orange
val AppleRed = Color(0xFFFF453A) // iOS System Red
val ApplePurple = Color(0xFFBF5AF2) // iOS System Purple
val AppleIndigo = Color(0xFF5E5CE6) // iOS System Indigo

// Apple Typography Labels
val AppleLabelPrimary = Color(0xFFFFFFFF) // High-contrast White
val AppleLabelSecondary = Color(0xFF8E8E93) // iOS System Gray Label
val AppleLabelTertiary = Color(0xFF636366) // iOS Tertiary Label
val AppleLabelQuaternary = Color(0xFF48484A) // iOS Quaternary Label

// Dynamic Frosted Glass Overlays
val AppleGlassSurface = Color(0xD91C1C1E) // 85% Frosted Gray 6
val AppleGlassHighlight = Color(0x1FFFFFFF) // Glass highlight specular sheen

// Backward-Compatibility Cinema Palette Mappings
val CinemaBackground = AppleBackground
val CinemaSurface = AppleSecondaryBackground
val CinemaSurfaceVariant = AppleTertiaryBackground
val CinemaBorder = AppleBorder
val CinemaBorderLight = AppleBorderLight

val CinemaPrimary = AppleGold
val CinemaOnPrimary = Color(0xFF1C1400)
val CinemaPrimaryContainer = Color(0xFF332700)
val CinemaOnPrimaryContainer = Color(0xFFFFF275)
val CinemaSecondary = Color(0xFFFFE066)
val CinemaAccentGlow = Color(0x33FFD60A)

val CinemaTextPrimary = AppleLabelPrimary
val CinemaTextSecondary = AppleLabelSecondary
val CinemaTextTertiary = AppleLabelTertiary

// Verdict Status Colors (Aligned with Apple System Indicators)
val VerdictIncluded = AppleGreen
val VerdictIncludedContainer = Color(0xFF0D3D1E)
val VerdictOnIncludedContainer = Color(0xFF85F5A9)

val VerdictRentBuy = AppleOrange
val VerdictRentBuyContainer = Color(0xFF4A2500)
val VerdictOnRentBuyContainer = Color(0xFFFFCC80)

val VerdictUnavailable = AppleRed
val VerdictUnavailableContainer = Color(0xFF4A100E)
val VerdictOnUnavailableContainer = Color(0xFFFFB4AB)

val VerdictUnknown = AppleLabelSecondary
val VerdictUnknownContainer = Color(0xFF2C2C2E)
val VerdictOnUnknownContainer = Color(0xFFE5E5EA)
