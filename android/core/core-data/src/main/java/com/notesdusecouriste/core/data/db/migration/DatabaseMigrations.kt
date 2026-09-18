package com.notesdusecouriste.core.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE interventions ADD COLUMN nom TEXT")
        db.execSQL("ALTER TABLE interventions ADD COLUMN prenom TEXT")
        db.execSQL("ALTER TABLE interventions ADD COLUMN age INTEGER")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE interventions ADD COLUMN photosConsentAcknowledged INTEGER NOT NULL DEFAULT 0",
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS intervention_photos (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                interventionId INTEGER NOT NULL,
                fileName TEXT NOT NULL,
                createdAtEpochMillis INTEGER NOT NULL,
                FOREIGN KEY(interventionId) REFERENCES interventions(id) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_intervention_photos_interventionId ON intervention_photos(interventionId)",
        )
    }
}
