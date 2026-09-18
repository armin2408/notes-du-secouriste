package com.notesdusecouriste.core.data.repository

import android.net.Uri
import androidx.room.withTransaction
import com.notesdusecouriste.core.data.db.AppDatabase
import com.notesdusecouriste.core.data.db.entity.InterventionPhotoEntity
import com.notesdusecouriste.core.data.model.InterventionPhoto
import com.notesdusecouriste.core.data.photos.InterventionPhotoStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterventionPhotoRepository @Inject constructor(
    private val database: AppDatabase,
    private val photoStore: InterventionPhotoStore,
) {
    private val photoDao = database.interventionPhotoDao()
    private val interventionDao = database.interventionDao()

    fun observePhotos(interventionId: Long): Flow<List<InterventionPhoto>> =
        photoDao.observeByInterventionId(interventionId).map { list ->
            list.map { it.toDomain() }
        }

    fun observePhotosConsent(interventionId: Long): Flow<Boolean> =
        interventionDao.observeById(interventionId).map { entity ->
            entity?.photosConsentAcknowledged == true
        }

    suspend fun acknowledgePhotosConsent(interventionId: Long) {
        interventionDao.updatePhotosConsent(interventionId, acknowledged = true)
    }

    suspend fun addPhotoFromUri(interventionId: Long, uri: Uri): InterventionPhoto =
        withContext(Dispatchers.IO) {
            val fileName = photoStore.importFromUri(uri)
            val id = photoDao.insert(
                InterventionPhotoEntity(
                    interventionId = interventionId,
                    fileName = fileName,
                    createdAtEpochMillis = System.currentTimeMillis(),
                ),
            )
            InterventionPhoto(
                id = id,
                interventionId = interventionId,
                fileName = fileName,
                createdAtEpochMillis = System.currentTimeMillis(),
            )
        }

    suspend fun deletePhoto(photoId: Long) {
        withContext(Dispatchers.IO) {
            val entity = photoDao.getById(photoId) ?: return@withContext
            photoDao.deleteById(photoId)
            photoStore.deleteFile(entity.fileName)
        }
    }

    suspend fun deleteAllForIntervention(interventionId: Long) {
        withContext(Dispatchers.IO) {
            val photos = photoDao.getByInterventionId(interventionId)
            photoDao.deleteByInterventionId(interventionId)
            photoStore.deleteFiles(photos.map { it.fileName })
        }
    }

    suspend fun deleteAllForInterventions(ids: Collection<Long>) {
        if (ids.isEmpty()) return
        withContext(Dispatchers.IO) {
            val idList = ids.toList()
            database.withTransaction {
                idList.forEach { interventionId ->
                    val photos = photoDao.getByInterventionId(interventionId)
                    photoDao.deleteByInterventionId(interventionId)
                    photoStore.deleteFiles(photos.map { it.fileName })
                }
            }
        }
    }

    fun resolveFilePath(fileName: String): String =
        photoStore.resolveFile(fileName).absolutePath

    private fun InterventionPhotoEntity.toDomain() = InterventionPhoto(
        id = id,
        interventionId = interventionId,
        fileName = fileName,
        createdAtEpochMillis = createdAtEpochMillis,
    )
}
