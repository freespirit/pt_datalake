package com.phototagger.datalake

import com.phototagger.datalake.data.PhotoRepository
import com.phototagger.datalake.model.Photo
import com.phototagger.datalake.storage.PhotoStorage

class PhotoService(val storage: PhotoStorage, val repository: PhotoRepository) {

    /**
     * @return the id of the stored photo
     */
    fun savePhoto(photo: Photo): String {
        val photoId = insertPhotoToDB(photo)
        val storageUrl = savePhotoToStorage(photo, photoId)

        val dbPhoto = PhotoRepository.Photo(photoId, photo.tags, photo.originalUrl, storageUrl)
        repository.update(dbPhoto)

        return photoId
    }

    private fun savePhotoToStorage(photo: Photo, photoId: String) =
            storage.save(photo.bytes, photoId)

    private fun insertPhotoToDB(photo: Photo): String {
        val dbPhoto = PhotoMapper().modelToDb(photo)
        return repository.add(dbPhoto)
    }

    fun getPhoto(photoId: String): Photo? {
        val dbPhoto = repository.get(photoId) ?: return null
        return PhotoMapper().dbToModel(dbPhoto).copy(bytes = storage.load(dbPhoto.storageUrl))
    }

    fun getAll(): List<String> =
            repository.getAll()
                    .map { it.id }

    fun update(photoId: String, photo: Photo) {
        val dbPhoto = PhotoMapper().modelToDb(photo)
                .copy(id = photoId)
        repository.update(dbPhoto)
    }

    fun delete(id: String) {
        repository.delete(id)
    }

}

class PhotoMapper {
    fun dbToModel(dbPhoto: PhotoRepository.Photo): Photo =
            Photo(byteArrayOf(), dbPhoto.tags, dbPhoto.originalUrl, dbPhoto.storageUrl)

    fun modelToDb(photo: Photo): PhotoRepository.Photo =
            PhotoRepository.Photo("", photo.tags, photo.originalUrl, photo.storageUrl)
}