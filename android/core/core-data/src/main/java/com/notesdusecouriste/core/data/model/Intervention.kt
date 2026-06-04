package com.notesdusecouriste.core.data.model

data class Intervention(
    val id: Long,
    val startedAtEpochMillis: Long,
    val status: InterventionStatus,
    val schemaVersion: String,
    val nom: String? = null,
    val prenom: String? = null,
    val age: Int? = null,
)
