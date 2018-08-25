package com.phototagger.datalake.data

import PhotoService
import com.phototagger.datalake.Photo
import com.phototagger.datalake.storage.LocalPhotoStorage
import com.phototagger.datalake.storage.PhotoStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.jvmName

internal class PhotoServiceTest {

    private lateinit var repository: PhotoRepository
    private lateinit var storage: PhotoStorage
    private lateinit var service: PhotoService

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        repository = mockk(PhotoDB::class.jvmName)
        storage = mockk<LocalPhotoStorage>()
        service = PhotoService(storage, repository)
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
    }

    @Test
    fun storePhoto_toDB() {
        val photo = Photo(ByteArray(0), listOf(), "http://example.com")
        val photoId = "1"

        every { repository.add(any()) } returns photoId

        assert(service.savePhoto(byteArrayOf(), listOf()) == photoId)
    }
}