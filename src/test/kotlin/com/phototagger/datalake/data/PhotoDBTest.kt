package com.phototagger.datalake.data

import com.phototagger.datalake.data.PhotoRepository.Photo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

const val SQLITE_DIR = "./data"
const val SQLITE_FILE = "test_photos.db"

internal class PhotoDBTest {
    private val pathToSqlite = FileSystems.getDefault().getPath(SQLITE_DIR)
    private val pathToSqliteFile = FileSystems.getDefault().getPath(pathToSqlite.toString(), SQLITE_FILE)
    private lateinit var db: PhotoDB

    @BeforeEach
    fun setUp() {
        Files.createDirectories(pathToSqlite)

        Files.deleteIfExists(pathToSqliteFile)
        Files.createFile(pathToSqliteFile)

        val sqlite = Paths.get(SQLITE_DIR, SQLITE_FILE).toString()
        db = PhotoDB(sqlite)
    }

    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(pathToSqliteFile)
        Files.deleteIfExists(pathToSqlite)
    }

    @Test
    fun add_inserts() {
        val photo = Photo("", listOf("tag1, tag2"), "example.com", "/tmp/photo1")

        assertDoesNotThrow { db.add(photo) }
    }

    @Test
    fun get_none() {
        val photo: Photo? = db.get("1")
        assert(photo == null)
    }

    @Test
    fun getAll_none() {
        val photos = db.getAll()
        assert(photos.isEmpty())
    }

    @Test
    fun add_and_get() {
        val photoIn = Photo("", listOf("tag1, tag2"), "example.com", "/tmp/photo1")
        val id = db.add(photoIn)
        val photoOut = db.get(id)

        assertNotNull(photoOut)

        if (photoOut != null) {
            assertArrayEquals(photoIn.tags.toTypedArray(), photoOut.tags.toTypedArray())
            assertEquals(photoIn.originalUrl, photoOut.originalUrl)
            assertEquals(photoIn.storageUrl, photoOut.storageUrl)
        }
    }

    @Test
    fun update_none() {
        assertDoesNotThrow {
            db.update(Photo("1", listOf("tag1", "tag2"), "example.com", "/tmp/photo1"))
        }
    }

    @Test
    fun update_added() {
        val photoIn = Photo("", listOf("tag1", "tag2"), "example.com", "/tmp/photo1")
        val id = db.add(photoIn)

        var photoOut = db.get(id)

        assertNotNull(photoOut)
        assertEquals("example.com", photoOut?.originalUrl)
        assertEquals("/tmp/photo1", photoOut?.storageUrl)
        assertArrayEquals(listOf("tag1", "tag2").toTypedArray(), photoOut?.tags?.toTypedArray())


        db.update(Photo(id, listOf("tag1"), "example.co.uk", "/tmp/photo2"))
        photoOut = db.get(id)

        assertNotNull(photoOut)
        assertEquals("example.co.uk", photoOut?.originalUrl)
        assertEquals("/tmp/photo2", photoOut?.storageUrl)
        assertArrayEquals(listOf("tag1").toTypedArray(), photoOut?.tags?.toTypedArray())
    }

    @Test
    fun delete_none() {
        assertDoesNotThrow {
            db.delete("123")
        }
    }

    @Test
    fun delete_added() {
        val photoIn = Photo("", listOf("tag1", "tag2"), "example.com", "/tmp/photo1")
        val id = db.add(photoIn)


        assertNotNull(db.get(id))
        assertDoesNotThrow {
            db.delete(id)
        }
        assertNull(db.get(id))

    }
}