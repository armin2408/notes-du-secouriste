package com.notesdusecouriste.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences",
)

@Singleton
class ThemePreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val policyKey = stringPreferencesKey("theme_default_policy")
    private val lastThemeKey = stringPreferencesKey("last_explicit_theme")

    val preferences: Flow<ThemePreferences> =
        context.themeDataStore.data.map { prefs ->
            ThemePreferences(
                defaultPolicy = ThemeDefaultPolicy.fromStorage(
                    prefs[policyKey] ?: ThemeDefaultPolicy.SYSTEM.storageValue,
                ),
                lastExplicitTheme = ExplicitTheme.fromStorage(
                    prefs[lastThemeKey] ?: ExplicitTheme.DARK.storageValue,
                ),
            )
        }

    suspend fun setDefaultPolicy(policy: ThemeDefaultPolicy) {
        context.themeDataStore.edit { it[policyKey] = policy.storageValue }
    }

    suspend fun setLastExplicitTheme(theme: ExplicitTheme) {
        context.themeDataStore.edit { it[lastThemeKey] = theme.storageValue }
    }

    fun resolveIsDark(preferences: ThemePreferences, isSystemDark: Boolean): Boolean =
        when (preferences.defaultPolicy) {
            ThemeDefaultPolicy.SYSTEM -> isSystemDark
            ThemeDefaultPolicy.LAST_CHOSEN -> preferences.lastExplicitTheme == ExplicitTheme.DARK
        }
}
