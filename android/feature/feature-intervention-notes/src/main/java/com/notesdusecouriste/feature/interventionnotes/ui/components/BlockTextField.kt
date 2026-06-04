package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BlockTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    extraKeyboardOptions: KeyboardOptions? = null,
    readOnly: Boolean = false,
) {
    val options = extraKeyboardOptions ?: KeyboardOptions(keyboardType = keyboardType)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (singleLine) 56.dp else 96.dp),
        label = { Text(label) },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = singleLine,
        readOnly = readOnly,
        keyboardOptions = options,
    )
}
