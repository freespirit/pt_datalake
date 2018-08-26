package com.phototagger.datalake.data

interface PhotoRepository {

    /**
     * @return the id of the newly added photo
     */
    fun add(photo: Photo): String

    fun get(id: String): Photo?

    fun getAll(): List<Photo>

    fun update(photoId: String, photo: Photo)

    fun delete(id: String)

    data class Photo(val id: String, val tags: List<String>, val originalUrl: String, val storageUrl: String)
}