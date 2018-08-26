package com.phototagger.datalake.model

data class Photo(
        val bytes: ByteArray,
        val tags: List<String>,
        val originalUrl: String,
        val storageUrl: String)