package com.phototagger.datalake

data class Photo(val bytes: ByteArray, val tags: List<String>, val originalUrl: String)