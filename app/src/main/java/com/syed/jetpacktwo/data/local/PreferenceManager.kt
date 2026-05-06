package com.syed.jetpacktwo.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val BASE_URL_KEY = stringPreferencesKey("base_url")
        val DEVICE_ID_KEY = stringPreferencesKey("device_id")
        val DEVICE_NAME_KEY = stringPreferencesKey("device_name")
        val SCHEDULER_ID_KEY = stringPreferencesKey("scheduler_id")
        val EPC_FILTER_KEY = stringPreferencesKey("epc_filter")
        val IS_DARK_MODE_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("is_dark_mode")
        val DEFAULT_BASE_URL = "https://stockbotapi.technowavegroup.com/api/"
    }

    val baseUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[BASE_URL_KEY] ?: DEFAULT_BASE_URL
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE_KEY] ?: true // Default to Dark Mode as requested before
    }

    val deviceId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEVICE_ID_KEY] ?: ""
    }

    val deviceName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEVICE_NAME_KEY] ?: ""
    }

    val schedulerId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SCHEDULER_ID_KEY] ?: "1" // Default to 1
    }

    val epcFilter: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EPC_FILTER_KEY] ?: ""
    }

    suspend fun saveBaseUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[BASE_URL_KEY] = url
        }
    }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDark
        }
    }

    suspend fun saveDevice(id: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = id
            preferences[DEVICE_NAME_KEY] = name
        }
    }

    suspend fun saveSchedulerId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[SCHEDULER_ID_KEY] = id
        }
    }

    suspend fun saveEpcFilter(filter: String) {
        context.dataStore.edit { preferences ->
            preferences[EPC_FILTER_KEY] = filter
        }
    }
}
