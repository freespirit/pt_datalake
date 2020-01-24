package com.phototagger.datalake

import com.google.gson.Gson
import java.util.Base64

class PhotoDTO(val bytes: ByteArray,
               val tags: List<String>,
               val originalUrl: String)

private class EncodedPhoto(val bytes: String,
                           val tags: List<String>,
                           val originalUrl: String)

class PhotoSerializer {
    fun serialize(photo: PhotoDTO): String {
        val encodedBytes = Base64.getEncoder().encodeToString(photo.bytes)

        val encodedPhoto = EncodedPhoto(encodedBytes, photo.tags, photo.originalUrl)
        return Gson().toJson(encodedPhoto)
    }
}

class PhotoDeserializer {
    fun deserialize(content: String): PhotoDTO? {

        val encodedPhoto = Gson().fromJson<EncodedPhoto>(content, EncodedPhoto::class.java)

        val decodedBytes = Base64.getDecoder().decode(encodedPhoto.bytes)
        return PhotoDTO(decodedBytes, encodedPhoto.tags, encodedPhoto.originalUrl)
    }
}