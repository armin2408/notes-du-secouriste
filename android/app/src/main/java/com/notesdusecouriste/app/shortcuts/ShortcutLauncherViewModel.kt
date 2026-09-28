package com.notesdusecouriste.app.shortcuts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.repository.InterventionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShortcutLauncherViewModel @Inject constructor(
    private val interventionRepository: InterventionRepository,
) : ViewModel() {
    fun createNote(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            runCatching { interventionRepository.createDraftIntervention() }
                .onSuccess(onCreated)
        }
    }
}
