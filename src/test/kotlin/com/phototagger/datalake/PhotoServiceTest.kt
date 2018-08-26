package com.phototagger.datalake

import PhotoService
import com.phototagger.datalake.data.PhotoDB
import com.phototagger.datalake.data.PhotoRepository
import com.phototagger.datalake.model.Photo
import com.phototagger.datalake.storage.LocalPhotoStorage
import com.phototagger.datalake.storage.PhotoStorage
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.jvmName

internal class PhotoServiceTest {

    private lateinit var repository: PhotoRepository
    private lateinit var storage: PhotoStorage
    private lateinit var service: PhotoService

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        repository = mockk<PhotoDB>()
        storage = mockk<LocalPhotoStorage>()
        service = PhotoService(storage, repository)
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
    }

    @Test
    fun storePhoto_toDB() {
        val photo = Photo(ByteArray(0), listOf(), "http://example.com", "")
        val photoId = "1"

        every { repository.add(any()) } returns photoId
        every { repository.update(any(), any()) } just Runs
        every { storage.save(any(), any()) } returns "/tmp/1"

        assert(service.savePhoto(byteArrayOf(), listOf(), "example.com") == photoId)
    }
}