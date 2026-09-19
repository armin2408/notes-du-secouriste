package com.notesdusecouriste.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "onboarding_preferences",
)

@Singleton
class OnboardingPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
    private val lastSeenVersionCodeKey = intPreferencesKey("last_seen_version_code")

    val onboardingCompleted: Flow<Boolean> =
        context.onboardingDataStore.data.map { prefs ->
            prefs[onboardingCompletedKey] ?: false
        }

    val lastSeenVersionCode: Flow<Int> =
        context.onboardingDataStore.data.map { prefs ->
            prefs[lastSeenVersionCodeKey] ?: 0
        }

    suspend fun completeOnboarding(currentVersionCode: Int) {
        context.onboardingDataStore.edit { prefs ->
            prefs[onboardingCompletedKey] = true
            prefs[lastSeenVersionCodeKey] = currentVersionCode
        }
    }

    /**
     * Utilisateurs antérieurs à la feature onboarding : pas de carrousel forcé.
     * Si la clé n’existe pas encore et que des données locales existent → marquer complété.
     */
    suspend fun migrateLegacyUsersIfNeeded(
        currentVersionCode: Int,
        hasExistingLocalData: suspend () -> Boolean,
    ) {
        val data = context.onboardingDataStore.data.first()
        if (data.contains(onboardingCompletedKey)) return
        if (!hasExistingLocalData()) return
        completeOnboarding(currentVersionCode)
    }

    /** Bump silencieux : aligne la version vue sans UI. */
    suspend fun markVersionSeen(currentVersionCode: Int) {
        val current = lastSeenVersionCode.first()
        if (current >= currentVersionCode) return
        context.onboardingDataStore.edit { prefs ->
            prefs[lastSeenVersionCodeKey] = currentVersionCode
        }
    }
}
