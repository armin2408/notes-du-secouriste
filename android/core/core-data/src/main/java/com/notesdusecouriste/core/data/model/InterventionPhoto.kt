package com.notesdusecouriste.core.data.model

data class InterventionPhoto(
    val id: Long,
    val interventionId: Long,
    val fileName: String,
    val createdAtEpochMillis: Long,
)
