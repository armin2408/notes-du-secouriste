package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val hhmmFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val ssFormatter = DateTimeFormatter.ofPattern("ss")

@Composable
fun rememberMesuresClockAnnotatedString() =
    produceState(initialValue = buildClockAnnotated()) {
        while (true) {
            value = buildClockAnnotated()
            delay(1_000)
        }
    }.value

private fun buildClockAnnotated(): AnnotatedString = buildAnnotatedString {
    val now = LocalTime.now()
    append(now.format(hhmmFormatter))
    append(":")
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append(now.format(ssFormatter))
    }
}

@Composable
fun MesuresClockText() {
    Text(
        text = rememberMesuresClockAnnotatedString(),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

