package com.notesdusecouriste.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.preferences.GuardrailsPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardrailsViewModel @Inject constructor(
    private val guardrailsPreferencesRepository: GuardrailsPreferencesRepository,
) : ViewModel() {
    val privacyShortAcknowledged: StateFlow<Boolean> =
        guardrailsPreferencesRepository.privacyShortAcknowledged.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true, // évite un flash de dialog avant le premier emit
        )

    fun acknowledgePrivacyShort() {
        viewModelScope.launch {
            guardrailsPreferencesRepository.setPrivacyShortAcknowledged(true)
        }
    }
}
