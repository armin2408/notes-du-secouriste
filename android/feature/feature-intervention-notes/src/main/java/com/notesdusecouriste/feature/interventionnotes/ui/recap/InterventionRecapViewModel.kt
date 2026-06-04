package com.notesdusecouriste.feature.interventionnotes.ui.recap

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.model.formatNoteHeader
import com.notesdusecouriste.core.data.model.withNormalizedMesures
import com.notesdusecouriste.core.data.preferences.RecapPreferencesRepository
import com.notesdusecouriste.core.data.repository.InterventionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterventionRecapUiState(
    val headerTitle: String = "",
    val recap: InterventionRecap = InterventionRecap(),
    val mesuresColumnsExpanded: Boolean = false,
)

@HiltViewModel
class InterventionRecapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    repository: InterventionRepository,
    private val recapPreferences: RecapPreferencesRepository,
) : ViewModel() {
    private val interventionId: Long =
        checkNotNull(savedStateHandle.get<Long>("interventionId")) {
            "interventionId required"
        }

    private val recapContentState =
        repository.observeNoteContent(interventionId)
            .map { content ->
                val normalized = content.withNormalizedMesures()
                InterventionRecapUiState(
                    headerTitle = formatNoteHeader(normalized.victime),
                    recap = InterventionRecapBuilder.build(context, normalized),
                )
            }

    val uiState: StateFlow<InterventionRecapUiState> =
        combine(
            recapContentState,
            recapPreferences.mesuresColumnsExpanded,
        ) { content, columnsExpanded ->
            content.copy(mesuresColumnsExpanded = columnsExpanded)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InterventionRecapUiState(),
        )

    fun toggleMesuresColumnsExpanded() {
        viewModelScope.launch {
            val expanded = !uiState.value.mesuresColumnsExpanded
            recapPreferences.setMesuresColumnsExpanded(expanded)
        }
    }
}
