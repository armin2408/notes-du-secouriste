package com.notesdusecouriste.app.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.app.R
import com.notesdusecouriste.app.ui.theme.AppThemeViewModel
import com.notesdusecouriste.app.ui.theme.appThemeViewModel
import com.notesdusecouriste.core.data.preferences.ThemeDefaultPolicy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    themeViewModel: AppThemeViewModel = appThemeViewModel(),
) {
    val preferences by themeViewModel.preferences.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
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
                .padding(16.dp)
                .selectableGroup(),
        ) {
            Text(
                text = stringResource(R.string.settings_theme_section),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = stringResource(R.string.settings_theme_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            ThemePolicyOption(
                label = stringResource(R.string.settings_theme_system),
                description = stringResource(R.string.settings_theme_system_desc),
                selected = preferences.defaultPolicy == ThemeDefaultPolicy.SYSTEM,
                onSelect = { themeViewModel.setDefaultPolicy(ThemeDefaultPolicy.SYSTEM) },
            )
            ThemePolicyOption(
                label = stringResource(R.string.settings_theme_last_chosen),
                description = stringResource(R.string.settings_theme_last_chosen_desc),
                selected = preferences.defaultPolicy == ThemeDefaultPolicy.LAST_CHOSEN,
                onSelect = { themeViewModel.setDefaultPolicy(ThemeDefaultPolicy.LAST_CHOSEN) },
            )
        }
    }
}

@Composable
private fun ThemePolicyOption(
    label: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton,
            )
            .padding(vertical = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(selected = selected, onClick = null)
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
