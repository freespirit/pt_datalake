package com.phototagger.datalake.storage

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.FileSystems
import java.nio.file.Files



internal class LocalPhotoStorageTest {
    companion object Constants {
        private const val PHOTO_ID = "myPhoto123"
        private const val STORAGE_DIR = "/tmp/pt_datalake/photo_storage"
    }

    private val photoBytes = byteArrayOf(1, 2, 3)

    private val pathToStorage = FileSystems.getDefault().getPath(STORAGE_DIR)
    private val pathToFile = FileSystems.getDefault().getPath(STORAGE_DIR, PHOTO_ID)

    private lateinit var photoStorage: LocalPhotoStorage

    @BeforeEach
    fun setup() {
        photoStorage = LocalPhotoStorage(STORAGE_DIR)

        Files.deleteIfExists(pathToFile)
        Files.deleteIfExists(pathToStorage)
    }

    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(pathToFile)
        Files.deleteIfExists(pathToStorage)
    }

    @Test
    fun save() {
        Files.createDirectories(pathToStorage)

        photoStorage.save(photoBytes, PHOTO_ID)

        assertTrue(Files.exists(pathToFile))
        val actualBytes = Files.readAllBytes(pathToFile)
        assertArrayEquals(photoBytes, actualBytes)
    }

    @Test
    fun save_makeDirs() {
        photoStorage.save(photoBytes, PHOTO_ID)

        assertTrue(Files.exists(pathToFile))
        val actualBytes = Files.readAllBytes(pathToFile)
        assertArrayEquals(photoBytes, actualBytes)
    }

    @Test
    fun load() {
        Files.createDirectories(pathToStorage)
        Files.createFile(pathToFile)
        Files.write(pathToFile, photoBytes)

        val actualBytes = photoStorage.load(pathToFile.toString())

        assertArrayEquals(photoBytes, actualBytes)
    }

    @Test
    fun load_fileOutsideOfStorage() {
        val pathToFile = FileSystems.getDefault().getPath("/tmp/$PHOTO_ID")
        Files.deleteIfExists(pathToFile)
        Files.createFile(pathToFile)

        assertThrows<IllegalArgumentException> { photoStorage.load(pathToFile.toString()) }
    }
}