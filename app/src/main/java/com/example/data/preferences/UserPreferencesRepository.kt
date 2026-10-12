package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val COUNTRY_CODE = stringPreferencesKey("country_code")
        val SUBSCRIBED_PROVIDER_IDS = stringSetPreferencesKey("subscribed_provider_ids")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val CUSTOM_API_KEY = stringPreferencesKey("custom_api_key")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val MEDIA_TYPE_FILTER = stringPreferencesKey("media_type_filter")
    }

    val countryCode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COUNTRY_CODE] ?: "MY"
    }

    val subscribedProviderIds: Flow<Set<Int>> = context.dataStore.data.map { preferences ->
        val stringSet = preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS]
        if (stringSet == null) {
            // Default for Malaysia: Netflix (8), Disney+ Hotstar (337), Prime Video (119), Apple TV+ (350)
            setOf(8, 337, 119, 350)
        } else {
            stringSet.mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    val customApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CUSTOM_API_KEY] ?: ""
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setCountryCode(countryCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNTRY_CODE] = countryCode.uppercase()
        }
    }

    suspend fun setSubscribedProviderIds(providerIds: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] = providerIds.map { it.toString() }.toSet()
        }
    }

    suspend fun toggleSubscription(providerId: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS]?.mapNotNull { it.toIntOrNull() }?.toMutableSet()
                ?: mutableSetOf(8, 337, 119, 350)
            if (current.contains(providerId)) {
                current.remove(providerId)
            } else {
                current.add(providerId)
            }
            preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] = current.map { it.toString() }.toSet()
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setCustomApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CUSTOM_API_KEY] = key.trim()
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
