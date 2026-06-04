package com.notesdusecouriste.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intervention_notes")
data class InterventionNotesEntity(
    @PrimaryKey val interventionId: Long,
    val sectionsJson: String = "{}",
)
