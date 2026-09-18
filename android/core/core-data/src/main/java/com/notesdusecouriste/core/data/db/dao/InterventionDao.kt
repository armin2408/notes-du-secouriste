package com.notesdusecouriste.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notesdusecouriste.core.data.db.entity.InterventionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterventionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(intervention: InterventionEntity): Long

    @Query("SELECT * FROM interventions WHERE id = :id")
    suspend fun getById(id: Long): InterventionEntity?

    @Query("SELECT * FROM interventions WHERE id = :id")
    fun observeById(id: Long): Flow<InterventionEntity?>

    @Query("SELECT * FROM interventions ORDER BY startedAtEpochMillis DESC")
    fun observeAll(): Flow<List<InterventionEntity>>

    @Query(
        """
        UPDATE interventions
        SET nom = :nom, prenom = :prenom, age = :age
        WHERE id = :id
        """,
    )
    suspend fun updateIdentity(
        id: Long,
        nom: String?,
        prenom: String?,
        age: Int?,
    )

    @Query(
        """
        UPDATE interventions
        SET photosConsentAcknowledged = :acknowledged
        WHERE id = :id
        """,
    )
    suspend fun updatePhotosConsent(id: Long, acknowledged: Boolean)

    @Query("DELETE FROM interventions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM interventions WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}
