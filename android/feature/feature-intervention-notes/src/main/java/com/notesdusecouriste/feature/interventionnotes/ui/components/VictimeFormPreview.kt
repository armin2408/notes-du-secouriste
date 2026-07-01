package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.notesdusecouriste.core.ui.preview.FontScalePreviews
import com.notesdusecouriste.core.ui.preview.ResponsivePreviews
import com.notesdusecouriste.core.ui.preview.ThemePreviews
import com.notesdusecouriste.core.ui.theme.NotesDuSecouristeTheme

/**
 * Aperçu isolé du bloc VICTIME (formulaire de saisie). Reproduit la même disposition
 * que l'écran réel — notamment la [Row] pondérée date de naissance (0.62) / âge (0.38),
 * point sensible quand la largeur change. L'écran complet de saisie est piloté par un
 * ViewModel et ne se prévisualise pas directement ; ce bloc en couvre le risque layout.
 */
@Composable
private fun VictimeFormCardPreviewContent() {
    NoteSectionCard {
        BlockTextField(
            label = "Nom",
            value = "DURAND",
            onValueChange = {},
            singleLine = true,
            extraKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        )
        BlockTextField(
            label = "Prénom",
            value = "Camille",
            onValueChange = {},
            singleLine = true,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BoldLabelField(
                label = "Date de naissance",
                value = "12/03/1991",
                onValueChange = {},
                modifier = Modifier.weight(0.62f),
                keyboardType = KeyboardType.Number,
                boldFirstLetter = false,
            )
            BoldLabelField(
                label = "Âge",
                value = "34",
                onValueChange = {},
                modifier = Modifier.weight(0.38f),
                keyboardType = KeyboardType.Number,
                boldFirstLetter = false,
            )
        }
        BlockTextField(
            label = "Coordonnées",
            value = "06 12 34 56 78",
            onValueChange = {},
        )
    }
}

@ResponsivePreviews
@Composable
private fun VictimeFormResponsivePreview() {
    NotesDuSecouristeTheme {
        VictimeFormCardPreviewContent()
    }
}

@ThemePreviews
@Composable
private fun VictimeFormThemePreview() {
    NotesDuSecouristeTheme {
        VictimeFormCardPreviewContent()
    }
}

@FontScalePreviews
@Composable
private fun VictimeFormFontScalePreview() {
    NotesDuSecouristeTheme {
        VictimeFormCardPreviewContent()
    }
}
