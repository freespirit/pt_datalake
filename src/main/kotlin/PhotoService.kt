import com.phototagger.datalake.Photo
import com.phototagger.datalake.data.PhotoRepository
import com.phototagger.datalake.storage.PhotoStorage

class PhotoService(val storage: PhotoStorage, val repository: PhotoRepository) {

    /**
     * @return the id of the stored photo
     */
    fun savePhoto(photoBytes: ByteArray, tags: List<String>): String {
        val storageUrl = ""//storage.save(photoBytes)
        val photo = Photo(photoBytes, tags, storageUrl)
        return repository.add(photo)
    }

}