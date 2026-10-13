package com.example.ui.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AppleDynamicIsland
import com.example.ui.components.DynamicBannerMessage
import com.example.ui.components.applePressEffect
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.discover.DiscoverViewModel
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.search.SearchViewModel
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SettingsViewModel
import com.example.ui.screens.watchlist.WatchlistScreen
import com.example.ui.screens.watchlist.WatchlistViewModel
import com.example.ui.theme.AppleBackground
import com.example.ui.theme.AppleBorder
import com.example.ui.theme.AppleGold

// Apple Minimalist Dock Navigation Colors matching the app's theme
val AppleNavBackground = Color(0xF2161618) // Translucent deep Apple OLED charcoal
val AppleNavActiveAccent = AppleGold // #FFD60A - Theme Primary Cinema Gold
val AppleNavActiveGlow = Color(0x2BFFD60A) // 17% Theme Accent Glow
val AppleNavInactive = Color(0xFF8E8E93) // Apple System Gray Label
val AppleNavBorder = Color(0x30FFFFFF) // Hairline frosted specular stroke

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    data object Watchlist : Screen("watchlist", "Watchlist", Icons.Filled.Bookmark, Icons.Filled.BookmarkBorder)
    data object Discover : Screen("discover", "Discover", Icons.Filled.Whatshot, Icons.Outlined.Whatshot)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MainAppContainer(
    searchViewModel: SearchViewModel,
    watchlistViewModel: WatchlistViewModel,
    discoverViewModel: DiscoverViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Search.route

    val watchlistItems by watchlistViewModel.watchlistItems.collectAsStateWithLifecycle()
    val watchlistCount = watchlistItems.size

    var bannerMessage by remember { mutableStateOf<DynamicBannerMessage?>(null) }

    fun showFeedback(icon: ImageVector, text: String, tint: Color = AppleGold) {
        bannerMessage = DynamicBannerMessage(icon = icon, text = text, iconTint = tint)
    }

    // Request notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val screens = listOf(
        Screen.Search,
        Screen.Watchlist,
        Screen.Discover,
        Screen.Settings
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AppleBackground,
            bottomBar = {
                // Floating Island Pill Bottom Navigation Bar (Matching user screenshot)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .testTag("bottom_nav_bar"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(34.dp),
                                spotColor = Color(0xDD000000),
                                ambientColor = Color(0x88000000)
                            )
                            .clip(RoundedCornerShape(34.dp))
                            .background(AppleNavBackground)
                            .border(
                                width = 0.8.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x40FFFFFF),
                                        Color(0x14FFFFFF)
                                    )
                                ),
                                shape = RoundedCornerShape(34.dp)
                            )
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            screens.forEach { screen ->
                                val isSelected = currentRoute == screen.route

                                val iconScale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.15f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "nav_icon_scale"
                                )

                                val tabColor by androidx.compose.animation.animateColorAsState(
                                    targetValue = if (isSelected) AppleNavActiveAccent else AppleNavInactive,
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                    label = "nav_color"
                                )

                                val pillAlpha by animateFloatAsState(
                                    targetValue = if (isSelected) 1f else 0f,
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                    label = "nav_pill_alpha"
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(26.dp))
                                        .background(
                                            AppleNavActiveGlow.copy(alpha = 0.22f * pillAlpha)
                                        )
                                        .border(
                                            width = 0.6.dp,
                                            color = AppleNavActiveAccent.copy(alpha = 0.28f * pillAlpha),
                                            shape = RoundedCornerShape(26.dp)
                                        )
                                        .applePressEffect(scaleDown = 0.88f) {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                        .testTag("nav_item_${screen.route}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(contentAlignment = Alignment.TopEnd) {
                                            Icon(
                                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.title,
                                                tint = tabColor,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .scale(iconScale)
                                            )

                                            // Numbered Notification Badge matching theme
                                            if (screen == Screen.Watchlist && watchlistCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .offset(x = 8.dp, y = (-4).dp)
                                                        .size(17.dp)
                                                        .clip(CircleShape)
                                                        .background(AppleNavActiveAccent)
                                                        .border(1.dp, Color(0xFF161618), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = if (watchlistCount > 99) "99+" else watchlistCount.toString(),
                                                        color = Color(0xFF141000),
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        lineHeight = 10.sp
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = screen.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = tabColor,
                                            letterSpacing = (-0.15).sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Search.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(spring(dampingRatio = Spring.DampingRatioNoBouncy)) },
                exitTransition = { fadeOut(spring(dampingRatio = Spring.DampingRatioNoBouncy)) },
                popEnterTransition = { fadeIn(spring(dampingRatio = Spring.DampingRatioNoBouncy)) },
                popExitTransition = { fadeOut(spring(dampingRatio = Spring.DampingRatioNoBouncy)) }
            ) {
                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = searchViewModel,
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onShowNotification = { icon, text, tint -> showFeedback(icon, text, tint) }
                    )
                }

                composable(Screen.Watchlist.route) {
                    WatchlistScreen(
                        viewModel = watchlistViewModel,
                        onShowNotification = { icon, text, tint -> showFeedback(icon, text, tint) },
                        onNavigateToSearch = {
                            navController.navigate(Screen.Search.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable(Screen.Discover.route) {
                    DiscoverScreen(
                        viewModel = discoverViewModel,
                        onShowNotification = { icon, text, tint -> showFeedback(icon, text, tint) }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onShowNotification = { icon, text, tint -> showFeedback(icon, text, tint) }
                    )
                }
            }
        }

        // Apple Dynamic Island Floating Action Notification Banner
        AppleDynamicIsland(
            message = bannerMessage,
            onDismiss = { bannerMessage = null }
        )
    }
}
