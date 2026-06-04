package com.notesdusecouriste.core.data.model

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

private val dobFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun dateNaissanceDigitsOnly(raw: String): String =
    raw.filter { it.isDigit() }.take(8)

fun formatDateNaissanceDisplay(digits: String): String {
    val d = dateNaissanceDigitsOnly(digits)
    return buildString {
        d.forEachIndexed { index, char ->
            append(char)
            if (index == 1 || index == 3) append('/')
        }
    }
}

fun isDateNaissanceComplete(digits: String): Boolean =
    dateNaissanceDigitsOnly(digits).length == 8

fun calculateAgeFromDateNaissance(digits: String): String? {
    val d = dateNaissanceDigitsOnly(digits)
    if (!isDateNaissanceComplete(d)) return null
    val formatted = formatDateNaissanceDisplay(d)
    return runCatching {
        val date = LocalDate.parse(formatted, dobFormatter)
        val years = Period.between(date, LocalDate.now()).years
        if (years in 0..150) years.toString() else null
    }.getOrNull()
}

fun applyVictimeDateAndAge(
    victime: VictimeBlock,
    newDateNaissance: String,
): VictimeBlock {
    val digits = dateNaissanceDigitsOnly(newDateNaissance)
    val calculatedAge = calculateAgeFromDateNaissance(digits)
    return victime.copy(
        dateNaissance = digits,
        age = calculatedAge ?: victime.age,
        ageOverride = if (calculatedAge != null) false else victime.ageOverride,
    )
}

fun applyVictimeManualAge(victime: VictimeBlock, age: String): VictimeBlock {
    val digits = age.filter { it.isDigit() }.take(3)
    return victime.copy(age = digits, ageOverride = true)
}
