package com.notesdusecouriste.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notesdusecouriste.core.data.db.entity.InterventionPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterventionPhotoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(photo: InterventionPhotoEntity): Long

    @Query(
        """
        SELECT * FROM intervention_photos
        WHERE interventionId = :interventionId
        ORDER BY createdAtEpochMillis ASC
        """,
    )
    fun observeByInterventionId(interventionId: Long): Flow<List<InterventionPhotoEntity>>

    @Query("SELECT * FROM intervention_photos WHERE id = :id")
    suspend fun getById(id: Long): InterventionPhotoEntity?

    @Query("SELECT * FROM intervention_photos WHERE interventionId = :interventionId")
    suspend fun getByInterventionId(interventionId: Long): List<InterventionPhotoEntity>

    @Query("DELETE FROM intervention_photos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM intervention_photos WHERE interventionId = :interventionId")
    suspend fun deleteByInterventionId(interventionId: Long)

    @Query("DELETE FROM intervention_photos WHERE interventionId IN (:ids)")
    suspend fun deleteByInterventionIds(ids: List<Long>)
}
