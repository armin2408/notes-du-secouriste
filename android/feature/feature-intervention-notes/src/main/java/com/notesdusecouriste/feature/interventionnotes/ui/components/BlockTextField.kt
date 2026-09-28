package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

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
    capitalizeSentences: Boolean = false,
) {
    val options = extraKeyboardOptions ?: KeyboardOptions(
        keyboardType = keyboardType,
        capitalization = if (capitalizeSentences) KeyboardCapitalization.Sentences else KeyboardCapitalization.None,
    )
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            onValueChange(if (capitalizeSentences) capitalizeSentenceStarts(raw) else raw)
        },
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

/**
 * Met en majuscule la première lettre du texte et celle qui suit une fin de phrase
 * (`.`, `!`, `?` ou retour à la ligne). Longueur inchangée : le curseur ne bouge pas.
 */
internal fun capitalizeSentenceStarts(text: String): String {
    if (text.isEmpty()) return text
    val chars = text.toCharArray()
    var atSentenceStart = true
    for (i in chars.indices) {
        val c = chars[i]
        when {
            c.isLetter() -> {
                if (atSentenceStart && c.isLowerCase()) {
                    chars[i] = c.toString().uppercase(Locale.FRANCE).singleOrNull() ?: c
                }
                atSentenceStart = false
            }
            c == '.' || c == '!' || c == '?' || c == '\n' -> atSentenceStart = true
            c.isDigit() -> atSentenceStart = false
        }
    }
    return String(chars)
}
