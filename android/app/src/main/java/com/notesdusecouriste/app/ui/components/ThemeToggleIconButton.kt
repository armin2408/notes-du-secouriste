package com.notesdusecouriste.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.notesdusecouriste.app.R

@Composable
fun ThemeToggleIconButton(
    isDark: Boolean,
    onToggle: () -> Unit,
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
            contentDescription = stringResource(R.string.content_description_toggle_theme),
        )
    }
}
