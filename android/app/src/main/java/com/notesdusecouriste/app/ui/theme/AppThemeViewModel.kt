package com.notesdusecouriste.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.preferences.ExplicitTheme
import com.notesdusecouriste.core.data.preferences.ThemeDefaultPolicy
import com.notesdusecouriste.core.data.preferences.ThemePreferences
import com.notesdusecouriste.core.data.preferences.ThemePreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val themePreferencesRepository: ThemePreferencesRepository,
) : ViewModel() {
    private val sessionOverrideDark = MutableStateFlow<Boolean?>(null)
    private val isSystemDark = MutableStateFlow(true)

    val preferences: StateFlow<ThemePreferences> =
        themePreferencesRepository.preferences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemePreferences(),
        )

    val effectiveIsDark: StateFlow<Boolean> =
        combine(preferences, sessionOverrideDark, isSystemDark) { prefs, override, system ->
            override ?: themePreferencesRepository.resolveIsDark(prefs, system)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    fun updateSystemDark(isDark: Boolean) {
        isSystemDark.value = isDark
    }

    fun toggleTheme(currentIsDark: Boolean) {
        val newIsDark = !currentIsDark
        sessionOverrideDark.value = newIsDark
        viewModelScope.launch {
            themePreferencesRepository.setLastExplicitTheme(
                if (newIsDark) ExplicitTheme.DARK else ExplicitTheme.LIGHT,
            )
        }
    }

    fun setDefaultPolicy(policy: ThemeDefaultPolicy) {
        if (policy == ThemeDefaultPolicy.SYSTEM) {
            sessionOverrideDark.value = null
        }
        viewModelScope.launch {
            themePreferencesRepository.setDefaultPolicy(policy)
        }
    }
}
