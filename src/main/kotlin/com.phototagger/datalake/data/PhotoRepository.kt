package com.phototagger.datalake.data

interface PhotoRepository {

    /**
     * @return the id of the newly added photo
     */
    fun add(photo: Photo): String //TODO consider returning the photo itself

    fun get(photoId: String): Photo?

    fun getAll(): List<Photo>

    fun update(photo: Photo)

    fun delete(id: String)

    data class Photo(val id: String, val tags: List<String>, val originalUrl: String, val storageUrl: String)
}