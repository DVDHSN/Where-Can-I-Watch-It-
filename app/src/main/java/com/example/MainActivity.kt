package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.data.local.AppDatabase
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.TmdbRepository
import com.example.data.repository.WatchlistRepository
import com.example.notification.WatchlistNotificationHelper
import com.example.ui.navigation.MainAppContainer
import com.example.ui.screens.discover.DiscoverViewModel
import com.example.ui.screens.search.SearchViewModel
import com.example.ui.screens.settings.SettingsViewModel
import com.example.ui.screens.watchlist.WatchlistViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize system notification channel
        WatchlistNotificationHelper.createNotificationChannel(this)

        val database = AppDatabase.getInstance(this)
        val preferencesRepository = UserPreferencesRepository(this)
        val tmdbRepository = TmdbRepository(
            titleDao = database.titleDao(),
            watchlistDao = database.watchlistDao(),
            preferencesRepository = preferencesRepository
        )
        val watchlistRepository = WatchlistRepository(database.watchlistDao())

        setContent {
            MyApplicationTheme {
                val searchViewModel = remember {
                    SearchViewModel(
                        tmdbRepository = tmdbRepository,
                        watchlistRepository = watchlistRepository,
                        preferencesRepository = preferencesRepository
                    )
                }

                val watchlistViewModel = remember {
                    WatchlistViewModel(
                        watchlistRepository = watchlistRepository,
                        tmdbRepository = tmdbRepository,
                        preferencesRepository = preferencesRepository
                    )
                }

                val discoverViewModel = remember {
                    DiscoverViewModel(
                        tmdbRepository = tmdbRepository,
                        watchlistRepository = watchlistRepository,
                        preferencesRepository = preferencesRepository
                    )
                }

                val settingsViewModel = remember {
                    SettingsViewModel(
                        preferencesRepository = preferencesRepository,
                        titleDao = database.titleDao()
                    )
                }

                MainAppContainer(
                    searchViewModel = searchViewModel,
                    watchlistViewModel = watchlistViewModel,
                    discoverViewModel = discoverViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
