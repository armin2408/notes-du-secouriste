package com.notesdusecouriste.core.data.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val frenchTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun timeDigitsOnly(raw: String): String = raw.filter { it.isDigit() }.take(4)

fun formatTimeDigitsDisplay(digits: String): String {
    val d = timeDigitsOnly(digits)
    return when {
        d.isEmpty() -> ""
        d.length <= 2 -> d
        else -> "${d.take(2)}:${d.drop(2)}"
    }
}

fun isFrenchTimeInputValid(digits: String): Boolean {
    val d = timeDigitsOnly(digits)
    if (d.isEmpty()) return true
    if (d.length < 4) return isFrenchTimePartiallyValid(d)
    val hours = d.take(2).toIntOrNull() ?: return false
    val minutes = d.drop(2).toIntOrNull() ?: return false
    return hours in 0..23 && minutes in 0..59
}

fun shouldShowFrenchTimeInvalid(digits: String): Boolean {
    val d = timeDigitsOnly(digits)
    if (d.isEmpty()) return false
    return !isFrenchTimeInputValid(d)
}

fun parseFrenchTimeDigits(digits: String): LocalTime? {
    val d = timeDigitsOnly(digits)
    if (d.length != 4) return null
    val formatted = formatTimeDigitsDisplay(d)
    return runCatching { LocalTime.parse(formatted, frenchTimeFormatter) }.getOrNull()
}

/** N’accepte que les chiffres formant une heure partielle ou complète valide. */
fun filterFrenchTimeDigitsInput(raw: String): String {
    val incoming = timeDigitsOnly(raw)
    val accepted = StringBuilder()
    for (char in incoming) {
        val candidate = accepted.toString() + char
        if (candidate.length > 4) break
        if (candidate.length < 4) {
            if (isFrenchTimePartiallyValid(candidate)) accepted.append(char)
        } else if (isFrenchTimeInputValid(candidate)) {
            accepted.append(char)
        }
    }
    return accepted.toString()
}

private fun isFrenchTimePartiallyValid(digits: String): Boolean {
    when (digits.length) {
        1 -> return digits[0] <= '2'
        2 -> {
            val hours = digits.toIntOrNull() ?: return false
            return hours <= 23
        }
        3 -> {
            if (!isFrenchTimePartiallyValid(digits.take(2))) return false
            return digits[2] <= '5'
        }
        else -> return false
    }
}
