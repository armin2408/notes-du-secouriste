package com.notesdusecouriste.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.model.Intervention
import com.notesdusecouriste.core.data.repository.InterventionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DeleteConfirmRequest {
    data class Single(val id: Long) : DeleteConfirmRequest
    data class Multiple(val ids: Set<Long>) : DeleteConfirmRequest
}

data class HomeUiState(
    val isCreating: Boolean = false,
    val errorMessage: String? = null,
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val deleteConfirm: DeleteConfirmRequest? = null,
    val isDeleting: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val interventionRepository: InterventionRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val interventions: StateFlow<List<Intervention>> =
        interventionRepository.observeInterventions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    fun createIntervention(onCreated: (Long) -> Unit) {
        if (_uiState.value.isCreating || _uiState.value.isSelectionMode) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, errorMessage = null)
            runCatching {
                interventionRepository.createDraftIntervention()
            }.onSuccess { id ->
                _uiState.value = _uiState.value.copy(isCreating = false)
                onCreated(id)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    errorMessage = error.message ?: "Impossible de créer l'intervention",
                )
            }
        }
    }

    fun onInterventionLongPress(id: Long) {
        _uiState.update {
            it.copy(
                isSelectionMode = true,
                selectedIds = it.selectedIds + id,
                deleteConfirm = null,
            )
        }
    }

    fun toggleSelection(id: Long) {
        _uiState.update { state ->
            if (!state.isSelectionMode) return@update state
            val updated = state.selectedIds.toMutableSet()
            if (id in updated) updated.remove(id) else updated.add(id)
            state.copy(
                selectedIds = updated,
                isSelectionMode = updated.isNotEmpty() || state.isSelectionMode,
            ).let { next ->
                if (updated.isEmpty()) next.copy(isSelectionMode = false) else next
            }
        }
    }

    fun exitSelectionMode() {
        _uiState.update {
            it.copy(isSelectionMode = false, selectedIds = emptySet(), deleteConfirm = null)
        }
    }

    fun requestDeleteSingle(id: Long) {
        _uiState.update { it.copy(deleteConfirm = DeleteConfirmRequest.Single(id)) }
    }

    fun requestDeleteSelected() {
        val ids = _uiState.value.selectedIds
        if (ids.isEmpty()) return
        _uiState.update { it.copy(deleteConfirm = DeleteConfirmRequest.Multiple(ids)) }
    }

    fun dismissDeleteConfirm() {
        _uiState.update { it.copy(deleteConfirm = null) }
    }

    fun confirmDelete() {
        val request = _uiState.value.deleteConfirm ?: return
        if (_uiState.value.isDeleting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching {
                when (request) {
                    is DeleteConfirmRequest.Single ->
                        interventionRepository.deleteIntervention(request.id)
                    is DeleteConfirmRequest.Multiple ->
                        interventionRepository.deleteInterventions(request.ids)
                }
            }.onSuccess {
                _uiState.update {
                    HomeUiState(errorMessage = null)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteConfirm = request,
                        errorMessage = error.message ?: "Impossible de supprimer",
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
