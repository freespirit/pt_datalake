package com.phototagger.datalake.data

import com.phototagger.datalake.Photo

interface PhotoRepository {

    /**
     * @return the id of the newly added photo
     */
    fun add(photo: Photo): String

    fun get(id: String): Photo

    fun getAll(): List<Photo>

    fun update(photo: Photo)

    fun delete(id: String)
}