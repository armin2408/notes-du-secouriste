package com.notesdusecouriste.feature.interventionnotes.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.core.data.model.InterventionNoteContent
import com.notesdusecouriste.core.data.model.MesureEntry
import com.notesdusecouriste.core.data.model.dateNaissanceDigitsOnly
import com.notesdusecouriste.core.data.model.isFrenchDateInputValid
import com.notesdusecouriste.core.data.model.formatPrenomInput
import com.notesdusecouriste.core.data.model.QuestionnairesBlock
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.components.BlockTextField
import com.notesdusecouriste.feature.interventionnotes.ui.components.BoldLabelField
import com.notesdusecouriste.feature.interventionnotes.ui.components.DateNaissanceVisualTransformation
import com.notesdusecouriste.feature.interventionnotes.ui.components.LocalPinnedHeaderKeys
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionCard
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteStickyHeaderRegistry
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind
import com.notesdusecouriste.feature.interventionnotes.ui.components.StackedStickyHeadersOverlay
import com.notesdusecouriste.feature.interventionnotes.ui.components.StickySectionHeader
import com.notesdusecouriste.feature.interventionnotes.ui.components.MesuresClockText
import com.notesdusecouriste.feature.interventionnotes.ui.components.computePinnedHeaderStack
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteMesuresBlockHeader
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteMesureSubSectionHeader
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteSectionHeader
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteSectionItem

private data class MesureDateTimeEditRequest(
    val entry: MesureEntry,
    val mode: MesureDateTimeEditMode,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterventionNotesListContent(
    viewModel: InterventionNotesViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val content = uiState.content
    var tabActionEntry by remember { mutableStateOf<MesureEntry?>(null) }
    var dateTimeEdit by remember { mutableStateOf<MesureDateTimeEditRequest?>(null) }
    var deleteConfirmEntry by remember { mutableStateOf<MesureEntry?>(null) }
    val tabActionsSheetState = rememberMesureTabActionsSheetState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val registry = remember { NoteStickyHeaderRegistry() }
    val listItemSpacingPx = with(LocalDensity.current) { 8.dp.roundToPx() }

    val pinnedKeys by remember(listState, registry.slots.size, registry.parentBlockHeightPx, listItemSpacingPx) {
        derivedStateOf {
            computePinnedHeaderStack(
                layoutInfo = listState.layoutInfo,
                slots = registry.slots,
                parentBlockHeightPx = registry.parentBlockHeightPx,
                listItemSpacingPx = listItemSpacingPx,
            )
                .map { it.slot.key }
                .toSet()
        }
    }

    tabActionEntry?.let { entry ->
        MesureTabActionsSheet(
            entry = entry,
            allEntries = content.mesures.entries,
            sheetState = tabActionsSheetState,
            onDismiss = { tabActionEntry = null },
            onEditTime = {
                tabActionEntry = null
                dateTimeEdit = MesureDateTimeEditRequest(entry, MesureDateTimeEditMode.TimeOnly)
            },
            onEditDate = {
                tabActionEntry = null
                dateTimeEdit = MesureDateTimeEditRequest(entry, MesureDateTimeEditMode.DateOnly)
            },
            onDelete = {
                tabActionEntry = null
                deleteConfirmEntry = entry
            },
        )
    }

    dateTimeEdit?.let { request ->
        MesureDateTimeDialog(
            initialEpochMillis = request.entry.horodatageEpochMillis,
            mode = request.mode,
            onDismiss = { dateTimeEdit = null },
            onConfirm = { epoch ->
                viewModel.updateMesureDateTime(request.entry.id, epoch)
                dateTimeEdit = null
            },
        )
    }

    deleteConfirmEntry?.let { entry ->
        MesureDeleteConfirmDialog(
            tabLabel = formatMesureTabLabel(entry.horodatageEpochMillis, content.mesures.entries),
            onDismiss = { deleteConfirmEntry = null },
            onConfirm = {
                viewModel.deleteMesuresAtHorodatage(entry.id)
                deleteConfirmEntry = null
            },
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalPinnedHeaderKeys provides pinnedKeys) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                // Espacement contrôlé manuellement par section (Spacers),
                // pour coller titres/onglets/sous-chapitres à l'intérieur de MESURES.
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                registry.reset()
                victimeSection(content, viewModel, context, registry)
                mesuresSection(
                    entries = content.mesures.entries,
                    selectedIndex = uiState.selectedMesureTabIndex,
                    viewModel = viewModel,
                    context = context,
                    registry = registry,
                    onTabLongPress = { tabActionEntry = it },
                )
                questionnairesSection(
                    questionnaires = content.questionnaires,
                    viewModel = viewModel,
                    context = context,
                    registry = registry,
                )
                commentaireSection(content, viewModel, context, registry)
            }
        }

        StackedStickyHeadersOverlay(
            listState = listState,
            registry = registry,
            contentPadding = contentPadding,
        )
    }
}

private fun LazyListScope.victimeSection(
    content: InterventionNoteContent,
    viewModel: InterventionNotesViewModel,
    context: Context,
    registry: NoteStickyHeaderRegistry,
) {
    val v = content.victime
    noteSectionHeader(
        key = "header_victime",
        title = context.getString(R.string.block_victime),
        kind = NoteSectionKind.Victime,
        registry = registry,
    )
    noteSectionItem(key = "victime_body", registry = registry) {
        NoteSectionCard {
            BlockTextField(
                label = stringResource(R.string.field_nom),
                value = v.nom,
                onValueChange = { value ->
                    viewModel.updateVictime { it.copy(nom = value.uppercase()) }
                },
                singleLine = true,
                extraKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            )
            BlockTextField(
                label = stringResource(R.string.field_prenom),
                value = v.prenom,
                onValueChange = { value ->
                    viewModel.updateVictime { it.copy(prenom = formatPrenomInput(value)) }
                },
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BoldLabelField(
                    label = stringResource(R.string.field_date_naissance),
                    value = dateNaissanceDigitsOnly(v.dateNaissance),
                    onValueChange = viewModel::updateVictimeDateNaissance,
                    modifier = Modifier.weight(0.62f),
                    keyboardType = KeyboardType.Number,
                    boldFirstLetter = false,
                    visualTransformation = DateNaissanceVisualTransformation(),
                    isInputValid = isFrenchDateInputValid(v.dateNaissance),
                )
                BoldLabelField(
                    label = stringResource(R.string.field_age),
                    value = v.age,
                    onValueChange = viewModel::updateVictimeAge,
                    modifier = Modifier.weight(0.38f),
                    keyboardType = KeyboardType.Number,
                    boldFirstLetter = false,
                )
            }
            BlockTextField(
                label = stringResource(R.string.field_coordonnees),
                value = v.coordonnees,
                onValueChange = { value -> viewModel.updateVictime { it.copy(coordonnees = value) } },
            )
        }
    }

    // Marge moyenne avant le chapitre suivant (MESURES).
    item(key = "spacer_after_victime") {
        androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
    }
    registry.advance()
}

private fun LazyListScope.mesuresSection(
    entries: List<MesureEntry>,
    selectedIndex: Int,
    viewModel: InterventionNotesViewModel,
    context: Context,
    registry: NoteStickyHeaderRegistry,
    onTabLongPress: (MesureEntry) -> Unit,
) {
    if (entries.isEmpty()) {
        noteSectionHeader(
            key = "header_mesures",
            title = context.getString(R.string.block_mesures),
            kind = NoteSectionKind.Mesures,
            registry = registry,
        )
        noteSectionItem(key = "mesures_empty", registry = registry) {
            NoteSectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.mesures_empty_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        onClick = viewModel::noteFirstMesure,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Text(stringResource(R.string.mesures_note_first))
                    }
                }
            }
        }
        // Marge moyenne avant le chapitre suivant.
        item(key = "spacer_after_mesures_empty") {
            androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
        }
        registry.advance()
        return
    }

    noteMesuresBlockHeader(
        key = "header_mesures_tabs",
        title = context.getString(R.string.block_mesures),
        registry = registry,
        header = {
            StickySectionHeader(
                title = context.getString(R.string.block_mesures),
                kind = NoteSectionKind.Mesures,
                trailing = { MesuresClockText() },
            )
        },
    ) {
        MesureTabsRow(
            entries = entries,
            selectedIndex = selectedIndex,
            onSelectTab = viewModel::selectMesureTab,
            onAddTab = viewModel::addMesureEntry,
            onTabLongPress = onTabLongPress,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    val entry = entries.getOrNull(selectedIndex) ?: return
    mesureEntryLazySections(entry, viewModel, context, registry)

    // Marge moyenne avant le chapitre suivant (QUESTIONNAIRES).
    item(key = "spacer_after_mesures") {
        androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
    }
    registry.advance()
}

private fun LazyListScope.commentaireSection(
    content: InterventionNoteContent,
    viewModel: InterventionNotesViewModel,
    context: Context,
    registry: NoteStickyHeaderRegistry,
) {
    noteSectionHeader(
        key = "header_commentaire",
        title = context.getString(R.string.block_commentaire),
        kind = NoteSectionKind.Commentaire,
        registry = registry,
    )
    noteSectionItem(key = "commentaire_body", registry = registry) {
        NoteSectionCard {
            BlockTextField(
                label = stringResource(R.string.field_commentaire),
                value = content.commentaire,
                onValueChange = viewModel::updateCommentaire,
                singleLine = false,
            )
        }
    }
}

private fun LazyListScope.questionnairesSection(
    questionnaires: QuestionnairesBlock,
    viewModel: InterventionNotesViewModel,
    context: Context,
    registry: NoteStickyHeaderRegistry,
) {
    noteSectionHeader(
        key = "header_questionnaires",
        title = "QUESTIONNAIRES",
        kind = NoteSectionKind.Questionnaires,
        registry = registry,
    )

    // SAMPLE
    val sampleExpanded = viewModel.isQuestionnaireSubSectionExpanded(NoteSectionKind.Sample)
    noteMesureSubSectionHeader(
        key = "header_sample",
        title = "SAMPLE",
        kind = NoteSectionKind.Sample,
        registry = registry,
        expanded = sampleExpanded,
        onToggleExpanded = { viewModel.toggleQuestionnaireSubSection(NoteSectionKind.Sample) },
    )
    if (sampleExpanded) {
        noteSectionItem(key = "sample_body", registry = registry) {
            NoteSectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BlockTextField(
                        label = "S — Signes / Symptômes",
                        value = questionnaires.sample.signesSymptomes,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(sample = it.questionnaires.sample.copy(signesSymptomes = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "A — Allergies",
                        value = questionnaires.sample.allergies,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(sample = it.questionnaires.sample.copy(allergies = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "M — Médicaments",
                        value = questionnaires.sample.medicaments,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(sample = it.questionnaires.sample.copy(medicaments = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "P — Antécédents",
                        value = questionnaires.sample.antecedents,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(sample = it.questionnaires.sample.copy(antecedents = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "L — Dernier repas / boisson",
                        value = questionnaires.sample.dernierRepas,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(sample = it.questionnaires.sample.copy(dernierRepas = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "E — Événements",
                        value = questionnaires.sample.evenements,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(sample = it.questionnaires.sample.copy(evenements = v))) }
                        },
                        singleLine = false,
                    )
                }
            }
        }
    }

    // OPQRST
    val opqrstExpanded = viewModel.isQuestionnaireSubSectionExpanded(NoteSectionKind.Opqrst)
    noteMesureSubSectionHeader(
        key = "header_opqrst",
        title = "OPQRST",
        kind = NoteSectionKind.Opqrst,
        registry = registry,
        expanded = opqrstExpanded,
        onToggleExpanded = { viewModel.toggleQuestionnaireSubSection(NoteSectionKind.Opqrst) },
    )
    if (opqrstExpanded) {
        noteSectionItem(key = "opqrst_body", registry = registry) {
            NoteSectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BlockTextField(
                        label = "O — Début",
                        value = questionnaires.opqrst.debut,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(opqrst = it.questionnaires.opqrst.copy(debut = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "P — Provocation / Soulagement",
                        value = questionnaires.opqrst.provocationPalliation,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(opqrst = it.questionnaires.opqrst.copy(provocationPalliation = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "Q — Qualité",
                        value = questionnaires.opqrst.qualite,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(opqrst = it.questionnaires.opqrst.copy(qualite = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "R — Région",
                        value = questionnaires.opqrst.region,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(opqrst = it.questionnaires.opqrst.copy(region = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "S — Sévérité",
                        value = questionnaires.opqrst.severite,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(opqrst = it.questionnaires.opqrst.copy(severite = v))) }
                        },
                        singleLine = false,
                    )
                    BlockTextField(
                        label = "T — Temps / Évolution",
                        value = questionnaires.opqrst.temps,
                        onValueChange = { v ->
                            viewModel.updateContent { it.copy(questionnaires = it.questionnaires.copy(opqrst = it.questionnaires.opqrst.copy(temps = v))) }
                        },
                        singleLine = false,
                    )
                }
            }
        }
    }

    // Marge moyenne avant le chapitre suivant (COMMENTAIRE).
    item(key = "spacer_after_questionnaires") {
        androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
    }
    registry.advance()
}
