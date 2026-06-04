package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.model.CirculationMesure
import com.notesdusecouriste.core.data.model.ConscienceMesure
import com.notesdusecouriste.core.data.model.GlasgowMesure
import com.notesdusecouriste.core.data.model.InterventionNoteContent
import com.notesdusecouriste.core.data.model.MesureEntry
import com.notesdusecouriste.core.data.model.MesuresBlock
import com.notesdusecouriste.core.data.model.RespirationMesure
import com.notesdusecouriste.core.data.model.SuspicionAvcMesure
import com.notesdusecouriste.core.data.model.VictimeBlock
import com.notesdusecouriste.core.data.model.applyVictimeDateAndAge
import com.notesdusecouriste.core.data.model.dateNaissanceDigitsOnly
import com.notesdusecouriste.core.data.model.applyVictimeManualAge
import com.notesdusecouriste.core.data.model.formatNoteHeader
import com.notesdusecouriste.core.data.model.normalized
import com.notesdusecouriste.core.data.model.withNormalizedMesures
import com.notesdusecouriste.core.data.repository.InterventionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterventionNotesUiState(
    val content: InterventionNoteContent = InterventionNoteContent(),
    val headerTitle: String = "Nouvelle note",
    val selectedMesureTabIndex: Int = 0,
    val isSaving: Boolean = false,
    val lastSavedAtEpochMillis: Long? = null,
    val errorMessage: String? = null,
    /** Sous-chapitres dépliés (non persisté), clé = "$scopeId|${kind.name}". */
    val expandedSubSections: Set<String> = emptySet(),
)

@HiltViewModel
class InterventionNotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: InterventionRepository,
) : ViewModel() {
    private val interventionId: Long =
        checkNotNull(savedStateHandle.get<Long>("interventionId")) {
            "interventionId required"
        }

    private val _uiState = MutableStateFlow(InterventionNotesUiState())
    val uiState: StateFlow<InterventionNotesUiState> = _uiState.asStateFlow()

    private var saveJob: Job? = null
    private var suppressRemoteUpdate = false

    private val questionnairesScopeId = "questionnaires"
    private val mesuresScopeId = "mesures"

    init {
        viewModelScope.launch {
            repository.observeNoteContent(interventionId).collect { remote ->
                if (suppressRemoteUpdate) return@collect
                applyContent(remote.withNormalizedMesures())
            }
        }
    }

    private fun applyContent(content: InterventionNoteContent) {
        val normalized = content.withNormalizedVictime()
        _uiState.update { state ->
            val entries = normalized.mesures.entries
            val selectedId = state.content.mesures.entries
                .getOrNull(state.selectedMesureTabIndex)
                ?.id
            val tabIndex = when {
                entries.isEmpty() -> 0
                selectedId != null -> {
                    val idx = entries.indexOfFirst { it.id == selectedId }
                    if (idx >= 0) idx else state.selectedMesureTabIndex.coerceIn(0, entries.lastIndex)
                }
                else -> state.selectedMesureTabIndex.coerceIn(0, entries.lastIndex)
            }
            state.copy(
                content = normalized,
                headerTitle = formatNoteHeader(normalized.victime),
                selectedMesureTabIndex = tabIndex,
            )
        }
    }

    private fun InterventionNoteContent.withNormalizedVictime(): InterventionNoteContent {
        val digits = dateNaissanceDigitsOnly(victime.dateNaissance)
        return if (digits == victime.dateNaissance) {
            this
        } else {
            copy(victime = victime.copy(dateNaissance = digits))
        }
    }

    fun updateContent(transform: (InterventionNoteContent) -> InterventionNoteContent) {
        val updated = transform(_uiState.value.content).withNormalizedMesures()
        applyContent(updated)
        scheduleSave(updated)
    }

    fun updateVictime(transform: (VictimeBlock) -> VictimeBlock) {
        updateContent { content ->
            content.copy(victime = transform(content.victime))
        }
    }

    fun updateVictimeDateNaissance(value: String) {
        updateVictime { applyVictimeDateAndAge(it, value) }
    }

    fun updateVictimeAge(value: String) {
        updateVictime { applyVictimeManualAge(it, value) }
    }

    fun updateMesures(transform: (MesuresBlock) -> MesuresBlock) {
        updateContent { content ->
            content.copy(mesures = transform(content.mesures).normalized())
        }
    }

    fun noteFirstMesure() {
        if (_uiState.value.content.mesures.entries.isNotEmpty()) return
        addMesureEntry()
    }

    fun addMesureEntry() {
        val newEntry = MesureEntry.create()
        updateMesures { block ->
            block.copy(entries = block.entries + newEntry)
        }
        _uiState.update { state ->
            val idx = state.content.mesures.entries.indexOfFirst { it.id == newEntry.id }
            state.copy(
                selectedMesureTabIndex = if (idx >= 0) idx else state.content.mesures.entries.lastIndex.coerceAtLeast(0),
            )
        }
    }

    fun selectMesureTab(index: Int) {
        val size = _uiState.value.content.mesures.entries.size
        if (size == 0 || index !in 0 until size) return
        _uiState.update { it.copy(selectedMesureTabIndex = index) }
    }

    fun updateMesureDateTime(entryId: String, epochMillis: Long) {
        updateMesureEntry(entryId) { it.copy(horodatageEpochMillis = epochMillis) }
    }

    /** Supprime le(s) relevé(s) partageant la même date/heure que l’onglet ciblé. */
    fun deleteMesuresAtHorodatage(entryId: String) {
        val entries = _uiState.value.content.mesures.entries
        val target = entries.find { it.id == entryId } ?: return
        val epoch = target.horodatageEpochMillis
        val deletedIndex = entries.indexOfFirst { it.id == entryId }
        if (deletedIndex < 0) return

        updateMesures { block ->
            block.copy(entries = block.entries.filter { it.horodatageEpochMillis != epoch })
        }
        _uiState.update { state ->
            val remaining = state.content.mesures.entries
            val newIndex = when {
                remaining.isEmpty() -> 0
                deletedIndex <= state.selectedMesureTabIndex ->
                    (state.selectedMesureTabIndex - 1).coerceAtLeast(0)
                        .coerceAtMost(remaining.lastIndex)
                else -> state.selectedMesureTabIndex.coerceIn(0, remaining.lastIndex)
            }
            state.copy(selectedMesureTabIndex = newIndex)
        }
    }

    fun updateMesureEntry(entryId: String, transform: (MesureEntry) -> MesureEntry) {
        updateMesures { block ->
            block.copy(
                entries = block.entries.map { entry ->
                    if (entry.id == entryId) transform(entry) else entry
                },
            )
        }
    }

    fun updateRespiration(entryId: String, transform: (RespirationMesure) -> RespirationMesure) {
        updateMesureEntry(entryId) { it.copy(respiration = transform(it.respiration)) }
    }

    fun updateCirculation(entryId: String, transform: (CirculationMesure) -> CirculationMesure) {
        updateMesureEntry(entryId) { it.copy(circulation = transform(it.circulation)) }
    }

    fun updateConscience(entryId: String, transform: (ConscienceMesure) -> ConscienceMesure) {
        updateMesureEntry(entryId) { it.copy(conscience = transform(it.conscience)) }
    }

    fun updateGlasgow(entryId: String, transform: (GlasgowMesure) -> GlasgowMesure) {
        updateMesureEntry(entryId) { it.copy(glasgow = transform(it.glasgow)) }
    }

    fun updateSuspicionAvc(entryId: String, transform: (SuspicionAvcMesure) -> SuspicionAvcMesure) {
        updateMesureEntry(entryId) { it.copy(suspicionAvc = transform(it.suspicionAvc)) }
    }

    fun updateCommentaire(value: String) {
        updateContent { it.copy(commentaire = value) }
    }

    fun toggleMesureSubSection(kind: com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind) {
        toggleSubSectionExpanded(mesuresScopeId, kind)
    }

    fun isMesureSubSectionExpanded(
        kind: com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind,
    ): Boolean = isSubSectionExpanded(mesuresScopeId, kind)

    fun isQuestionnaireSubSectionExpanded(
        kind: com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind,
    ): Boolean = isSubSectionExpanded(questionnairesScopeId, kind)

    fun toggleQuestionnaireSubSection(kind: com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind) {
        toggleSubSectionExpanded(questionnairesScopeId, kind)
    }

    private fun isSubSectionExpanded(
        scopeId: String,
        kind: com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind,
    ): Boolean = expandedKey(scopeId, kind) in _uiState.value.expandedSubSections

    private fun toggleSubSectionExpanded(
        scopeId: String,
        kind: com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind,
    ) {
        val key = expandedKey(scopeId, kind)
        _uiState.update { state ->
            val next = state.expandedSubSections.toMutableSet()
            if (key in next) next.remove(key) else next.add(key)
            state.copy(expandedSubSections = next)
        }
    }

    private fun expandedKey(scopeId: String, kind: com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind): String =
        "$scopeId|${kind.name}"

    private fun scheduleSave(content: InterventionNoteContent) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            delay(400)
            suppressRemoteUpdate = true
            runCatching {
                repository.saveNoteContent(interventionId, content)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        lastSavedAtEpochMillis = System.currentTimeMillis(),
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Échec de l'enregistrement",
                    )
                }
            }
            suppressRemoteUpdate = false
        }
    }
}
