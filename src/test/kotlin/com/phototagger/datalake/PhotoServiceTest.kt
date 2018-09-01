package com.phototagger.datalake

import com.phototagger.datalake.data.PhotoDB
import com.phototagger.datalake.data.PhotoRepository
import com.phototagger.datalake.model.Photo
import com.phototagger.datalake.storage.LocalPhotoStorage
import com.phototagger.datalake.storage.PhotoStorage
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PhotoServiceTest {

    private lateinit var repository: PhotoRepository
    private lateinit var storage: PhotoStorage
    private lateinit var service: PhotoService

    @BeforeEach
    fun setUp() {
        repository = mockk<PhotoDB>()
        storage = mockk<LocalPhotoStorage>()
        service = PhotoService(storage, repository)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun storePhoto_toDB() {
        val photo = Photo(ByteArray(0), listOf(), "http://example.com", "")
        val photoId = "1"

        every { repository.add(any()) } returns photoId
        every { repository.update(any()) } just Runs
        every { storage.save(any(), any()) } returns "/tmp/1"

        assert(service.savePhoto(photo) == photoId)
    }

    @Test
    fun getPhoto_none() {
        every { repository.get(any()) } returns null

        val photoId = "1"
        val photo = service.getPhoto(photoId)

        assertEquals(null, photo)
    }

    @Test
    fun getPhoto_fromDB() {
        val photoId = "1"
        val dbPhoto = PhotoRepository.Photo(photoId, listOf("tag1", "tag2"), "example.com", "/tmp/photos/1")

        every { repository.get(photoId) } returns dbPhoto
        every { storage.load(any()) } returns byteArrayOf()

        val photo = service.getPhoto(photoId)

        assertNotNull(photo)

        assertEquals("example.com", photo!!.originalUrl)
        assertEquals("/tmp/photos/1", photo.storageUrl)
        assertArrayEquals(listOf("tag1", "tag2").toTypedArray(), photo.tags.toTypedArray())

    }

    @Test
    fun getAll_isEmpty() {
        every { repository.getAll() } returns emptyList()

        val photos: List<Photo> = service.getAll()

        assert(photos.isEmpty())
    }

    @Test
    fun getAll_emptyDoesntThrow() {
        every { repository.getAll() } returns emptyList()
        every { storage.load(any()) } returns byteArrayOf()

        assertDoesNotThrow { service.getAll() }
    }

    @Test
    fun getAll_returnsAll() {
        val photo1 = PhotoRepository.Photo("1", listOf("tag1", "tag2"), "example.com", "/tmp/photos/1")
        val photo2 = PhotoRepository.Photo("2", listOf("tag3", "tag4"), "example.org", "/tmp/photos/2")

        every { repository.getAll() } returns listOf(photo1, photo2)
        every { storage.load(any()) } returns byteArrayOf()

        val photos = service.getAll()

        assertArrayEquals(arrayOf("tag1", "tag2"), photos[0].tags.toTypedArray())
        assertArrayEquals(arrayOf("tag3", "tag4"), photos[1].tags.toTypedArray())

        assertEquals("example.com", photos[0].originalUrl)
        assertEquals("example.org", photos[1].originalUrl)

        assertEquals("/tmp/photos/1", photos[0].storageUrl)
        assertEquals("/tmp/photos/2", photos[1].storageUrl)
    }

    @Test
    fun update_none() {
        val photo = Photo(byteArrayOf(), listOf(), "", "")

        every { repository.update(any()) } just Runs

        assertDoesNotThrow { service.update("1", photo) }
    }

    @Test
    fun delete_none() {
        every { repository.delete(any()) } just Runs
        assertDoesNotThrow { service.delete("1") }
    }

}