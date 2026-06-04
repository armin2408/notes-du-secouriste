package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.notesdusecouriste.feature.interventionnotes.ui.GlasgowOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesureDropdownField(
    label: String,
    options: List<GlasgowOption>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    optionLabel: @Composable (GlasgowOption) -> String,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = options.find { it.key == selectedKey.trim() }
    val displayText = if (selected != null) optionLabel(selected) else ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    onClick = {
                        onSelect(option.key)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
