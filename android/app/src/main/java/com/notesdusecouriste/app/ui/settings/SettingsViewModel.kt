package com.notesdusecouriste.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.preferences.SecouristeProfile
import com.notesdusecouriste.core.data.preferences.SecouristeProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    repository: SecouristeProfileRepository,
) : ViewModel() {
    val profile: StateFlow<SecouristeProfile> = repository.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SecouristeProfile(),
    )
}
