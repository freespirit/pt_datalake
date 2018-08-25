package com.phototagger.datalake.storage

import org.jetbrains.annotations.NotNull
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

class LocalPhotoStorage(path: String) : PhotoStorage {

    private val pathToStorage: Path = FileSystems.getDefault().getPath(path)

    override fun save(@NotNull photoBytes: ByteArray, @NotNull photoId: String): String {
        val pathToFile = FileSystems.getDefault().getPath(pathToStorage.toString(), photoId)

        Files.createDirectories(pathToStorage)

        return Files
                .write(pathToFile, photoBytes)
                .toAbsolutePath()
                .toString()
    }

    override fun load(location: String): ByteArray {
        val pathToFile = FileSystems.getDefault().getPath(location)

        if(!pathToFile.startsWith(pathToStorage)) {
            throw IllegalArgumentException("Illegal path to file received - $location. ${this::class} works in $pathToStorage only")
        }

        return Files.readAllBytes(pathToFile)
    }
}