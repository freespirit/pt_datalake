package com.phototagger.datalake.domain

import com.phototagger.datalake.PhotoDTO
import com.phototagger.datalake.PhotoDtoMapper
import com.phototagger.datalake.PhotoService

class CreatePhotoUseCase(private val service: PhotoService) : CompletableUseCase<PhotoDTO, String> {

    override fun execute(input: PhotoDTO, onSuccess: (output: String) -> Unit, onError: (error: Exception) -> Unit) {
        try {
            val photo = PhotoDtoMapper().mapDtoToModel(input)
            val photoId = service.savePhoto(photo)
            onSuccess(photoId)
        } catch (error: Exception) {
            onError(error)
        }
    }
}