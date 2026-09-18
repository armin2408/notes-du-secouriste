package com.notesdusecouriste.app.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecouristeProfileScreen(
    onBack: () -> Unit,
    viewModel: SecouristeProfileViewModel = hiltViewModel(),
) {
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val saveStatus by viewModel.saveStatus.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.profile_title))
                        val statusText = when (saveStatus) {
                            ProfileSaveStatus.Saving -> stringResource(R.string.profile_saving)
                            ProfileSaveStatus.Saved -> stringResource(R.string.profile_saved_auto)
                            ProfileSaveStatus.Idle -> null
                        }
                        if (statusText != null) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.profile_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            ProfileField(
                label = stringResource(R.string.profile_prenom),
                value = draft.prenom,
                onValueChange = viewModel::updatePrenom,
                capitalization = KeyboardCapitalization.Words,
            )
            ProfileField(
                label = stringResource(R.string.profile_nom),
                value = draft.nom,
                onValueChange = viewModel::updateNom,
                capitalization = KeyboardCapitalization.Characters,
            )
            ProfileField(
                label = stringResource(R.string.profile_contact),
                value = draft.contact,
                onValueChange = viewModel::updateContact,
                keyboardType = KeyboardType.Email,
                singleLine = true,
            )
            ProfileField(
                label = stringResource(R.string.profile_organisme),
                value = draft.organisme,
                onValueChange = viewModel::updateOrganisme,
                capitalization = KeyboardCapitalization.Sentences,
            )
            ProfileField(
                label = stringResource(R.string.profile_competences),
                value = draft.competences,
                onValueChange = viewModel::updateCompetences,
                capitalization = KeyboardCapitalization.Sentences,
                singleLine = false,
                minLines = 3,
            )
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType,
        ),
    )
}
