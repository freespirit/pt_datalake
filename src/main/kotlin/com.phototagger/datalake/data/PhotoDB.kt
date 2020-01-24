package com.phototagger.datalake.data

import com.phototagger.datalake.data.PhotoRepository.Photo
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

/**
 * An 'Exposed' (local Database) implementation of [PhotoRepository]. See [https://github.com/JetBrains/Exposed] for technical details on `Exposed`
 */

object Photos : IntIdTable() {
    val url: Column<String> = varchar("url", 256)
    val storageUrl: Column<String?> = varchar("storageUrl", 256).nullable()
}

object Tags : IntIdTable() {
    val name: Column<String> = varchar("name", 50)
}

object PhotoTags : IntIdTable() {
    val photoId: Column<EntityID<Int>> = reference("photoId", Photos.id)
    val tagId: Column<EntityID<Int>> = reference("tagId", Tags.id).index("tagIdIndex", isUnique = false)
}

class PhotoDB(pathToSQLite: String) : PhotoRepository {

    private val dbConnection = Database.connect("jdbc:sqlite:$pathToSQLite", driver = "org.sqlite.JDBC")

    init {
        transaction(Connection.TRANSACTION_SERIALIZABLE, 3, dbConnection) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Photos)
            SchemaUtils.create(Tags)
            SchemaUtils.create(PhotoTags)
        }
    }

    private fun <T> transact(statement: Transaction.() -> T): T = transaction(Connection.TRANSACTION_SERIALIZABLE, 3, dbConnection) {
        addLogger(StdOutSqlLogger)
        statement()
    }

    override fun add(photo: Photo): String = transact {
        val id = Photos.insertAndGetId {
            it[Photos.url] = photo.originalUrl
            it[Photos.storageUrl] = photo.storageUrl
        }

        insertPhotoTags(photo, id)

        id.toString()
    }

    private fun insertPhotoTags(photo: Photo, photoId: EntityID<Int>) {
        for (tag in photo.tags) {
            //TODO insert OR get
            val tagId = Tags.insertAndGetId {
                it[Tags.name] = tag
            }

            PhotoTags.insert {
                it[PhotoTags.photoId] = photoId
                it[PhotoTags.tagId] = tagId
            }
        }
    }

    private fun deletePhotoTags(photoId: EntityID<Int>) {
        transact {
            PhotoTags.deleteWhere { PhotoTags.photoId eq photoId }
        }
    }

    override fun get(photoId: String): Photo? = transact {
        Photos.select { Photos.id eq photoId.toInt() }
                .map {
                    val id = it[Photos.id]
                    val tags = photoTags(id)
                    val originalUrl = it[Photos.url]
                    val storageUrl = it[Photos.storageUrl] ?: ""
                    Photo(id.toString(), tags, originalUrl, storageUrl)
                }
                .firstOrNull()

    }


    private fun photoTags(photoId: EntityID<Int>): List<String> = transact {
        val tagIds = PhotoTags
                .slice(PhotoTags.tagId)
                .select { PhotoTags.photoId eq photoId }
                .map { it[PhotoTags.tagId] }
                .toList()

        Tags.slice(Tags.name)
                .select { Tags.id inList tagIds }
                .map { it[Tags.name] }
    }

    override fun getAll(): List<Photo> = transact {
        Photos.selectAll()
                .map {
                    val id = it[Photos.id]
                    val tags = arrayListOf<String>()//photoTags(id)
                    val originalUrl = it[Photos.url]
                    val storageUrl = it[Photos.storageUrl] ?: ""
                    Photo(id.toString(), tags, originalUrl, storageUrl)
                }
    }

    override fun update(photo: Photo) {
        transact {
            val foundPhotoId = Photos.slice(Photos.id)
                    .select { Photos.id eq photo.id.toInt() }
                    .map { it[Photos.id] }
                    .firstOrNull()

            foundPhotoId?.let { id ->
                Photos.update({ Photos.id eq id }) {
                    it[url] = photo.originalUrl
                    it[storageUrl] = photo.storageUrl
                }
                deletePhotoTags(id)
                insertPhotoTags(photo, id)
            }

        }
    }

    override fun delete(id: String) {
        transact {
            Photos.deleteWhere { Photos.id eq id.toInt() }
        }
    }
}