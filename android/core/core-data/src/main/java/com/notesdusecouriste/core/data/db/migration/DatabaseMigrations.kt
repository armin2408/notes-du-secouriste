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
