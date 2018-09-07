package com.phototagger.datalake

import com.google.gson.Gson

class PhotoDTO(val bytes: ByteArray,
               val tags: List<String>,
               val originalUrl: String)

class PhotoSerializer {
    fun serialize(photo: PhotoDTO): String = Gson().toJson(photo)
}

class PhotoDeserializer {
    fun deserialize(content: String): PhotoDTO? = Gson().fromJson<PhotoDTO>(content, PhotoDTO::class.java)
}