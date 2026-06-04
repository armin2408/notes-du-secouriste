package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.core.data.model.GlasgowMesure
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.GlasgowChoices
import com.notesdusecouriste.feature.interventionnotes.ui.colors
import com.notesdusecouriste.feature.interventionnotes.ui.interpretationRes
import com.notesdusecouriste.feature.interventionnotes.ui.scoreSeverity
import com.notesdusecouriste.feature.interventionnotes.ui.totalScore

@Composable
fun GlasgowMesureSection(
    glasgow: GlasgowMesure,
    onOuvertureYeuxChange: (String) -> Unit,
    onReponseVerbaleChange: (String) -> Unit,
    onReponseMotriceChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FieldLabelText(
            label = stringResource(R.string.field_glasgow),
            boldFirstLetter = false,
            modifier = Modifier.padding(start = 4.dp),
        )
        GlasgowScoreSummary(glasgow = glasgow)

        MesureDropdownField(
            label = stringResource(R.string.glasgow_ouverture_yeux),
            options = GlasgowChoices.ouvertureYeux,
            selectedKey = glasgow.ouvertureYeux,
            onSelect = onOuvertureYeuxChange,
            optionLabel = { option ->
                stringResource(
                    R.string.glasgow_option_format,
                    option.score,
                    stringResource(option.labelRes),
                )
            },
            placeholder = stringResource(R.string.glasgow_dropdown_placeholder),
        )
        MesureDropdownField(
            label = stringResource(R.string.glasgow_reponse_verbale),
            options = GlasgowChoices.reponseVerbale,
            selectedKey = glasgow.reponseVerbale,
            onSelect = onReponseVerbaleChange,
            optionLabel = { option ->
                stringResource(
                    R.string.glasgow_option_format,
                    option.score,
                    stringResource(option.labelRes),
                )
            },
            placeholder = stringResource(R.string.glasgow_dropdown_placeholder),
        )
        MesureDropdownField(
            label = stringResource(R.string.glasgow_reponse_motrice),
            options = GlasgowChoices.reponseMotrice,
            selectedKey = glasgow.reponseMotrice,
            onSelect = onReponseMotriceChange,
            optionLabel = { option ->
                stringResource(
                    R.string.glasgow_option_format,
                    option.score,
                    stringResource(option.labelRes),
                )
            },
            placeholder = stringResource(R.string.glasgow_dropdown_placeholder),
        )
    }
}

@Composable
private fun GlasgowScoreSummary(glasgow: GlasgowMesure) {
    val total = glasgow.totalScore()
    val scoreColors = glasgow.scoreSeverity().colors()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = scoreColors.container,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = if (total != null) {
                    stringResource(R.string.glasgow_score_total, total)
                } else {
                    stringResource(R.string.glasgow_score_incomplete)
                },
                style = MaterialTheme.typography.titleMedium,
                color = scoreColors.onContainer,
            )
            Text(
                text = if (total != null) {
                    stringResource(checkNotNull(glasgow.interpretationRes()))
                } else {
                    stringResource(R.string.glasgow_score_hint)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = scoreColors.onContainer.copy(alpha = 0.9f),
            )
        }
    }
}
