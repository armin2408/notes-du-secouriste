package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.notesdusecouriste.core.data.model.formatTimeDigitsDisplay
import com.notesdusecouriste.core.data.model.timeDigitsOnly

class TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = timeDigitsOnly(text.text)
        val formatted = formatTimeDigitsDisplay(digits)
        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = TimeOffsetMapping(digits.length),
        )
    }
}

private class TimeOffsetMapping(
    private val digitCount: Int,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        val clamped = offset.coerceIn(0, digitCount)
        return if (clamped <= 2) clamped else clamped + 1
    }

    override fun transformedToOriginal(offset: Int): Int {
        val maxTransformed = if (digitCount <= 2) digitCount else digitCount + 1
        val clamped = offset.coerceIn(0, maxTransformed)
        return if (clamped <= 2) clamped else (clamped - 1).coerceAtMost(digitCount)
    }
}
