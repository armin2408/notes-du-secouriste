package com.notesdusecouriste.core.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val PrimaryTabHeight = 48.dp

/** Marge horizontale du libellé : l'indicateur « Primary » est calé sur la largeur du texte. */
val PrimaryTabHorizontalPadding = 16.dp

/**
 * Onglet « Primary tabs » (Material 3) avec appui long. Le `Tab` Material consomme le clic
 * avant tout `combinedClickable` posé dessus, d'où ce composant.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrimaryLongPressTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlay: @Composable () -> Unit = {},
) {
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .height(PrimaryTabHeight)
            .combinedClickable(
                role = Role.Tab,
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                },
            )
            .padding(horizontal = PrimaryTabHorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
        )
        overlay()
    }
}

/** Onglet « + » : pastille pleine couleur primaire, même hauteur que les onglets. */
@Composable
fun PrimaryAddTab(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(PrimaryTabHeight)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
