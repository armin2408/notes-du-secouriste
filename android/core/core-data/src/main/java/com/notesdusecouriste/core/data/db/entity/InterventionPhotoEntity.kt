package com.notesdusecouriste.core.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "intervention_photos",
    foreignKeys = [
        ForeignKey(
            entity = InterventionEntity::class,
            parentColumns = ["id"],
            childColumns = ["interventionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("interventionId")],
)
data class InterventionPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val interventionId: Long,
    /** Nom de fichier sous filesDir/intervention_photos/ (pas de chemin absolu). */
    val fileName: String,
    val createdAtEpochMillis: Long,
)
