package com.phototagger.datalake.domain

import com.phototagger.datalake.PhotoService

class GetAllPhotosUseCase(private val photoService: PhotoService) : CompletableUseCase<Any?, List<String>> {

    override fun execute(input: Any?, onSuccess: (output: List<String>) -> Unit, onError: (error: Exception) -> Unit) {
        try {
            onSuccess(photoService.getAll())
        } catch (error: Exception) {
            onError(error)
        }
    }
}