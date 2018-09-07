package com.phototagger.datalake.domain

import com.phototagger.datalake.PhotoDTO
import com.phototagger.datalake.PhotoDtoMapper
import com.phototagger.datalake.PhotoService

class GetPhotoUseCase(private val photoService: PhotoService) : CompletableUseCase<String, PhotoDTO?> {

    override fun execute(input: String, onSuccess: (output: PhotoDTO?) -> Unit, onError: (error: Exception) -> Unit) {
        try {
            val photo = photoService.getPhoto(input)
            val dtoPhoto = if (photo == null) null else PhotoDtoMapper().mapModelToDto(photo)
            onSuccess(dtoPhoto)
        } catch (error: Exception) {
            onError(error)
        }
    }
}