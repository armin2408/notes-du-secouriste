package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.notesdusecouriste.core.data.model.formatDateNaissanceDisplay

class DateNaissanceVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)
        val formatted = formatDateNaissanceDisplay(digits)
        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = DateNaissanceOffsetMapping(digits.length),
        )
    }
}

private class DateNaissanceOffsetMapping(
    private val digitCount: Int,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        val clamped = offset.coerceIn(0, digitCount)
        return when {
            clamped <= 2 -> clamped
            clamped <= 4 -> clamped + 1
            else -> clamped + 2
        }
    }

    override fun transformedToOriginal(offset: Int): Int {
        val maxTransformed = when {
            digitCount <= 2 -> digitCount
            digitCount <= 4 -> digitCount + 1
            else -> digitCount + 2
        }
        val clamped = offset.coerceIn(0, maxTransformed)
        return when {
            clamped <= 2 -> clamped
            clamped <= 5 -> (clamped - 1).coerceAtMost(digitCount)
            else -> (clamped - 2).coerceAtMost(digitCount)
        }
    }
}
