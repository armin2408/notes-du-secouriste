package com.notesdusecouriste.core.data.model

import java.util.Locale

/** Prénom : première lettre de chaque mot en majuscule. */
fun formatPrenomInput(raw: String): String =
    raw.split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word ->
            word.lowercase(Locale.ROOT).replaceFirstChar { char ->
                char.titlecase(Locale.ROOT)
            }
        }
