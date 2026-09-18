package com.notesdusecouriste.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notesdusecouriste.core.data.db.dao.InterventionDao
import com.notesdusecouriste.core.data.db.dao.InterventionNotesDao
import com.notesdusecouriste.core.data.db.dao.InterventionPhotoDao
import com.notesdusecouriste.core.data.db.entity.InterventionEntity
import com.notesdusecouriste.core.data.db.entity.InterventionNotesEntity
import com.notesdusecouriste.core.data.db.entity.InterventionPhotoEntity

@Database(
    entities = [
        InterventionEntity::class,
        InterventionNotesEntity::class,
        InterventionPhotoEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun interventionDao(): InterventionDao
    abstract fun interventionNotesDao(): InterventionNotesDao
    abstract fun interventionPhotoDao(): InterventionPhotoDao
}
