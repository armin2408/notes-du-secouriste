package com.notesdusecouriste.core.data.model

fun formatNoteHeader(victime: VictimeBlock): String {
    val nom = victime.nom.trim().uppercase()
    val prenom = victime.prenom.trim()
    val age = victime.age.trim()
    val hasIdentity = nom.isNotEmpty() || prenom.isNotEmpty() || age.isNotEmpty()

    if (!hasIdentity) return "Nouvelle note"

    val nameParts = buildList {
        if (nom.isNotEmpty()) add(nom)
        if (prenom.isNotEmpty()) add(prenom)
    }
    val nameLine = nameParts.joinToString(" ")
    val ageSuffix = if (age.isNotEmpty()) "$age ans" else ""
    return when {
        nameLine.isNotEmpty() && ageSuffix.isNotEmpty() -> "$nameLine, $ageSuffix"
        nameLine.isNotEmpty() -> nameLine
        else -> ageSuffix
    }
}

fun formatHomeIdentityLine(
    nom: String?,
    prenom: String?,
    age: Int?,
): String {
    val v = VictimeBlock(
        nom = nom.orEmpty(),
        prenom = prenom.orEmpty(),
        age = age?.toString().orEmpty(),
    )
    return formatNoteHeader(v)
}
