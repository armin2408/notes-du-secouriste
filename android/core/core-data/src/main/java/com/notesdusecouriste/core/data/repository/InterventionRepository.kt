package com.notesdusecouriste.core.data.repository

import androidx.room.withTransaction
import com.notesdusecouriste.core.data.db.AppDatabase
import com.notesdusecouriste.core.data.db.entity.InterventionEntity
import com.notesdusecouriste.core.data.db.entity.InterventionNotesEntity
import com.notesdusecouriste.core.data.model.Intervention
import com.notesdusecouriste.core.data.model.InterventionNoteContent
import com.notesdusecouriste.core.data.model.InterventionStatus
import com.notesdusecouriste.core.data.model.NotesSectionsCodec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterventionRepository @Inject constructor(
    private val database: AppDatabase,
) {
    private val interventionDao = database.interventionDao()
    private val notesDao = database.interventionNotesDao()

    fun observeInterventions(): Flow<List<Intervention>> =
        interventionDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }

    suspend fun getIntervention(id: Long): Intervention? =
        interventionDao.getById(id)?.toDomain()

    fun observeNoteContent(interventionId: Long): Flow<InterventionNoteContent> =
        notesDao.observeByInterventionId(interventionId).map { entity ->
            entity?.let { NotesSectionsCodec.decode(it.sectionsJson) } ?: InterventionNoteContent()
        }

    suspend fun saveNoteContent(interventionId: Long, content: InterventionNoteContent) {
        val victime = content.victime
        val nom = victime.nom.trim().uppercase().takeIf { it.isNotEmpty() }
        val prenom = victime.prenom.trim().takeIf { it.isNotEmpty() }
        val ageInt = victime.age.trim().toIntOrNull()

        database.withTransaction {
            interventionDao.updateIdentity(
                id = interventionId,
                nom = nom,
                prenom = prenom,
                age = ageInt,
            )
            notesDao.upsert(
                InterventionNotesEntity(
                    interventionId = interventionId,
                    sectionsJson = NotesSectionsCodec.encode(content),
                ),
            )
        }
    }

    suspend fun deleteIntervention(id: Long) {
        database.withTransaction {
            notesDao.deleteByInterventionId(id)
            interventionDao.deleteById(id)
        }
    }

    suspend fun deleteInterventions(ids: Set<Long>) {
        if (ids.isEmpty()) return
        val idList = ids.toList()
        database.withTransaction {
            notesDao.deleteByInterventionIds(idList)
            interventionDao.deleteByIds(idList)
        }
    }

    suspend fun createDraftIntervention(): Long {
        val now = System.currentTimeMillis()
        var newId = 0L
        database.withTransaction {
            newId = interventionDao.insert(
                InterventionEntity(
                    startedAtEpochMillis = now,
                    status = InterventionStatus.DRAFT.storageValue,
                    schemaVersion = "v2",
                ),
            )
            notesDao.upsert(InterventionNotesEntity(interventionId = newId))
        }
        return newId
    }

    private fun InterventionEntity.toDomain(): Intervention =
        Intervention(
            id = id,
            startedAtEpochMillis = startedAtEpochMillis,
            status = InterventionStatus.fromStorage(status),
            schemaVersion = schemaVersion,
            nom = nom,
            prenom = prenom,
            age = age,
        )
}
