package com.notesdusecouriste.app.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.notesdusecouriste.app.R

@Composable
fun RowScope.AideMemoireThemeActions(
    onAideMemoire: () -> Unit,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
) {
    IconButton(onClick = onAideMemoire) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = stringResource(R.string.content_description_aide_memoire),
        )
    }
    ThemeToggleIconButton(
        isDark = isDark,
        onToggle = onToggleTheme,
    )
}
