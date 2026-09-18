package com.notesdusecouriste.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.preferences.SecouristeProfile
import com.notesdusecouriste.core.data.preferences.SecouristeProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProfileSaveStatus {
    Idle,
    Saving,
    Saved,
}

@HiltViewModel
class SecouristeProfileViewModel @Inject constructor(
    private val repository: SecouristeProfileRepository,
) : ViewModel() {
    private val _draft = MutableStateFlow(SecouristeProfile())
    val draft: StateFlow<SecouristeProfile> = _draft.asStateFlow()

    private val _saveStatus = MutableStateFlow(ProfileSaveStatus.Idle)
    val saveStatus: StateFlow<ProfileSaveStatus> = _saveStatus.asStateFlow()

    private var saveJob: Job? = null
    private var suppressAutosave = true

    init {
        viewModelScope.launch {
            _draft.value = repository.profile.first()
            suppressAutosave = false
        }
    }

    fun updatePrenom(value: String) = updateDraft { it.copy(prenom = value) }
    fun updateNom(value: String) = updateDraft { it.copy(nom = value) }
    fun updateContact(value: String) = updateDraft { it.copy(contact = value) }
    fun updateOrganisme(value: String) = updateDraft { it.copy(organisme = value) }
    fun updateCompetences(value: String) = updateDraft { it.copy(competences = value) }

    private fun updateDraft(transform: (SecouristeProfile) -> SecouristeProfile) {
        _draft.update(transform)
        scheduleAutosave()
    }

    private fun scheduleAutosave() {
        if (suppressAutosave) return
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            _saveStatus.value = ProfileSaveStatus.Saving
            delay(400)
            repository.save(_draft.value)
            _saveStatus.value = ProfileSaveStatus.Saved
        }
    }
}
