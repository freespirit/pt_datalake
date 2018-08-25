package com.phototagger.datalake.storage

import org.jetbrains.annotations.NotNull

interface PhotoStorage {

    /**
     * @return the location of the newly stored photo, e.g. the url where it's located and where it could be gotten from
     */
    fun save(@NotNull photoBytes: ByteArray, @NotNull photoId: String): String

    /**
     * @return the bytes stored at the provided location
     */
    fun load(location: String): ByteArray
}