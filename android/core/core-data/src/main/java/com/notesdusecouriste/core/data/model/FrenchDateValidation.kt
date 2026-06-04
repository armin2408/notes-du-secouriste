package com.notesdusecouriste.core.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val frenchDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun isFrenchDateInputValid(digits: String): Boolean {
    val d = dateNaissanceDigitsOnly(digits)
    if (d.isEmpty()) return true
    return when {
        d.length < 8 -> isFrenchDatePartiallyValid(d)
        else -> parseFrenchDateDigits(d) != null
    }
}

fun shouldShowFrenchDateInvalid(digits: String): Boolean {
    val d = dateNaissanceDigitsOnly(digits)
    if (d.isEmpty()) return false
    return !isFrenchDateInputValid(d)
}

fun parseFrenchDateDigits(digits: String): LocalDate? {
    val d = dateNaissanceDigitsOnly(digits)
    if (d.length != 8) return null
    val formatted = formatDateNaissanceDisplay(d)
    return runCatching { LocalDate.parse(formatted, frenchDateFormatter) }.getOrNull()
}

private fun isFrenchDatePartiallyValid(digits: String): Boolean {
    when (digits.length) {
        1 -> return digits[0] in '0'..'3'
        2 -> {
            val day = digits.toIntOrNull() ?: return false
            return day in 1..31
        }
        3 -> {
            if (!isFrenchDatePartiallyValid(digits.take(2))) return false
            return digits[2] in '0'..'1'
        }
        4 -> {
            if (!isFrenchDatePartiallyValid(digits.take(2))) return false
            val month = digits.substring(2, 4).toIntOrNull() ?: return false
            return month in 1..12
        }
        else -> {
            if (!isFrenchDatePartiallyValid(digits.take(4))) return false
            return true
        }
    }
}
