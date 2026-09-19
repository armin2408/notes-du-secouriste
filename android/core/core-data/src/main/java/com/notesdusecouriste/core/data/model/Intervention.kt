package com.notesdusecouriste.core.data.model

data class Intervention(
    val id: Long,
    val startedAtEpochMillis: Long,
    val status: InterventionStatus,
    val schemaVersion: String,
    val nom: String? = null,
    val prenom: String? = null,
    val age: Int? = null,
    /** True s’il y a au moins une donnée exportable (notes ou photos). */
    val hasSynthesisContent: Boolean = false,
)
