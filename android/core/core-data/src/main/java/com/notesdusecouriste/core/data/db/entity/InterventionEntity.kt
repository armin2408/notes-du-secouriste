package com.notesdusecouriste.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interventions")
data class InterventionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAtEpochMillis: Long,
    val status: String,
    val schemaVersion: String = "v1",
    val closedAtEpochMillis: Long? = null,
    val nom: String? = null,
    val prenom: String? = null,
    val age: Int? = null,
)
