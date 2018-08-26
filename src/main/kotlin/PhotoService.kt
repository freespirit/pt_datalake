
import com.phototagger.datalake.data.PhotoRepository
import com.phototagger.datalake.storage.PhotoStorage

class PhotoService(val storage: PhotoStorage, val repository: PhotoRepository) {

    /**
     * @return the id of the stored photo
     */
    fun savePhoto(photoBytes: ByteArray, tags: List<String>, originalUrl: String): String {
        var photo = PhotoRepository.Photo("", tags, originalUrl, "")

        val photoId = repository.add(photo)
        val storageUrl = storage.save(photoBytes, photoId)

        photo = PhotoRepository.Photo("", tags, originalUrl, storageUrl)
        repository.update(photoId, photo)

        return photoId
    }

}