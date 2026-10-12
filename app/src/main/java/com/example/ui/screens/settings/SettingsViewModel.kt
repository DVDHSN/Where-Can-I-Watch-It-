package com.example.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.dao.TitleDao
import com.example.data.preferences.UserPreferencesRepository
import com.example.domain.model.CountryPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val titleDao: TitleDao
) : ViewModel() {

    val countryCode: StateFlow<String> = preferencesRepository.countryCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "MY")

    val subscribedProviderIds: StateFlow<Set<Int>> = preferencesRepository.subscribedProviderIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf(8, 337, 119, 350))

    val notificationsEnabled: StateFlow<Boolean> = preferencesRepository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val customApiKey: StateFlow<String> = preferencesRepository.customApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _cacheClearedMessage = MutableStateFlow<String?>(null)
    val cacheClearedMessage: StateFlow<String?> = _cacheClearedMessage.asStateFlow()

    val isBuildConfigKeyAvailable: Boolean =
        BuildConfig.TMDB_API_KEY.isNotBlank() && BuildConfig.TMDB_API_KEY != "YOUR_TMDB_API_KEY"

    fun setCountry(code: String) {
        viewModelScope.launch {
            preferencesRepository.setCountryCode(code)
            // Update default subscriptions for this country
            val country = CountryPresets.findByCode(code)
            val defaultSubs = country.popularProviders.filter { it.isDefaultSelected }.map { it.id }.toSet()
            preferencesRepository.setSubscribedProviderIds(defaultSubs)
        }
    }

    fun toggleSubscription(providerId: Int) {
        viewModelScope.launch {
            preferencesRepository.toggleSubscription(providerId)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun saveCustomApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepository.setCustomApiKey(key)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            titleDao.clearOldCache(threshold = System.currentTimeMillis() + 1000)
            _cacheClearedMessage.value = "Cache cleared successfully"
        }
    }

    fun dismissCacheMessage() {
        _cacheClearedMessage.value = null
    }
}
