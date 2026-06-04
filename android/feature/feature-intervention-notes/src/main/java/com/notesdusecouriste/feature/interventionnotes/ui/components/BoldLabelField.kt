package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.feature.interventionnotes.R

/** Libellé de champ avec première lettre en gras (aligné sur les OutlinedTextField). */
@Composable
fun FieldLabelText(
    label: String,
    modifier: Modifier = Modifier,
    boldFirstLetter: Boolean = true,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text = buildFieldLabelAnnotatedString(label, suffix = null, boldFirstLetter = boldFirstLetter),
        style = style,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun BoldLabelField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    suffixInLabel: Boolean = false,
    maxDigits: Int? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    boldFirstLetter: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isInputValid: Boolean = true,
    showValidationWhenNotEmpty: Boolean = true,
) {
    val showInvalid = showValidationWhenNotEmpty && value.isNotEmpty() && !isInputValid
    val effectiveKeyboard = when {
        maxDigits != null -> KeyboardType.Number
        else -> keyboardType
    }
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val filtered = if (maxDigits != null) {
                raw.filter { it.isDigit() }.take(maxDigits)
            } else {
                raw
            }
            onValueChange(filtered)
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (singleLine) 56.dp else 96.dp),
        label = {
            Text(
                text = if (suffixInLabel) {
                    buildFieldLabelAnnotatedString(label, suffix, boldFirstLetter)
                } else {
                    buildFieldLabelAnnotatedString(label, suffix = null, boldFirstLetter)
                },
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        suffix = if (!suffixInLabel && suffix != null) {
            { Text(suffix, style = MaterialTheme.typography.bodyLarge) }
        } else {
            null
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = singleLine,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = effectiveKeyboard),
        visualTransformation = visualTransformation,
        isError = showInvalid,
        trailingIcon = if (showInvalid) {
            {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = stringResource(R.string.field_invalid_format),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        } else {
            null
        },
    )
}

fun buildFieldLabelAnnotatedString(
    label: String,
    suffix: String? = null,
    boldFirstLetter: Boolean = true,
): AnnotatedString = buildAnnotatedString {
    if (label.isNotEmpty()) {
        if (boldFirstLetter) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(label.first().uppercase())
            }
            if (label.length > 1) append(label.drop(1))
        } else {
            append(label)
        }
    }
    suffix?.let { append(" : $it") }
}
