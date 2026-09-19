package com.notesdusecouriste.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notesdusecouriste.core.data.db.entity.InterventionNotesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterventionNotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(notes: InterventionNotesEntity)

    @Query("SELECT * FROM intervention_notes WHERE interventionId = :interventionId")
    fun observeByInterventionId(interventionId: Long): Flow<InterventionNotesEntity?>

    @Query("SELECT * FROM intervention_notes WHERE interventionId = :interventionId")
    suspend fun getByInterventionId(interventionId: Long): InterventionNotesEntity?

    @Query("SELECT * FROM intervention_notes")
    fun observeAll(): Flow<List<InterventionNotesEntity>>

    @Query("DELETE FROM intervention_notes WHERE interventionId = :interventionId")
    suspend fun deleteByInterventionId(interventionId: Long)

    @Query("DELETE FROM intervention_notes WHERE interventionId IN (:interventionIds)")
    suspend fun deleteByInterventionIds(interventionIds: List<Long>)
}
