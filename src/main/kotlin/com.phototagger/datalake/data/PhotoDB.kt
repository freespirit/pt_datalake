package com.phototagger.datalake.data

import com.phototagger.datalake.Photo

// e.g. https://github.com/JetBrains/Exposed
class PhotoDB : PhotoRepository {

    object DBHelper {
    }

    override fun get(id: String): Photo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): List<Photo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(photo: Photo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun add(photo: Photo): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}