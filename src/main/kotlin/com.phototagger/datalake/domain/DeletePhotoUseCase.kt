package com.phototagger.datalake.domain

import com.phototagger.datalake.PhotoService

class DeletePhotoUseCase(private val photoService: PhotoService) : CompletableUseCase<String, Any?> {

    override fun execute(input: String, onSuccess: (output: Any?) -> Unit, onError: (error: Exception) -> Unit) {
        try {
            photoService.delete(input)
            onSuccess(null)
        } catch (error: Exception) {
            onError(error)
        }
    }

}