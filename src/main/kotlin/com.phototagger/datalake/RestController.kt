package com.phototagger.datalake

import com.google.gson.Gson
import com.phototagger.datalake.data.PhotoDB
import com.phototagger.datalake.domain.CreatePhotoUseCase
import com.phototagger.datalake.domain.DeletePhotoUseCase
import com.phototagger.datalake.domain.GetAllPhotosUseCase
import com.phototagger.datalake.domain.GetPhotoUseCase
import com.phototagger.datalake.model.Photo
import com.phototagger.datalake.storage.LocalPhotoStorage
import io.javalin.Context
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.crud
import io.javalin.apibuilder.CrudHandler
import org.eclipse.jetty.http.HttpStatus

const val MIME_TYPE_JSON = "application/json"

class RestController constructor(
        private val port: Int,
        private val photoService: PhotoService) : CrudHandler {

    companion object {
        //TODO extract configuration
        fun main(args: Array<String>) {
            val photoService = PhotoService(LocalPhotoStorage("data/files"), PhotoDB("data/db"))
            val port = 7000

            RestController(port, photoService)
                    .createServer()
        }
    }

    fun createServer(): Javalin = Javalin.create()
            .defaultContentType(MIME_TYPE_JSON)
            .get("/") { ctx -> ctx.result("Hello World") }
            .routes {
                crud("photos/:photo-id", this)
            }
            .start(port)

    override fun create(ctx: Context) {
        val requestBody = ctx.body()
        if (requestBody.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
            return
        }

        val photoDto = PhotoDeserializer().deserialize(requestBody)
        if (photoDto == null) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
            return
        }

        val onSuccessStatement: (String) -> Unit = {
            ctx.status(HttpStatus.CREATED_201)
                    .header("Location", buildRelativePhotoUrl(ctx.url(), it))
        }

        CreatePhotoUseCase(photoService)
                .execute(photoDto, onSuccessStatement) {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                }
    }

    private fun buildRelativePhotoUrl(baseUrl: String, id: String): String = "$baseUrl/$id"
    override fun getAll(ctx: Context) {
        val onSuccessStatement: (List<String>) -> Unit = {
            val urls = it.map { foundId -> buildRelativePhotoUrl(ctx.url(), foundId) }
            val jsonResponse = Gson().toJson(urls)
            ctx.result(jsonResponse)
        }

        GetAllPhotosUseCase(photoService)
                .execute(null, onSuccessStatement) {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                }
    }

    override fun getOne(ctx: Context, resourceId: String) {

        val onSuccessStatement: (PhotoDTO?) -> Unit = {
            when (it) {
                null -> ctx.status(HttpStatus.NOT_FOUND_404)
                else -> ctx.status(HttpStatus.OK_200)
                        .result(PhotoSerializer().serialize(it))
            }
        }

        GetPhotoUseCase(photoService)
                .execute(resourceId, onSuccessStatement) {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                }
    }

    override fun update(ctx: Context, resourceId: String) {
        // As a first iteration the update functionality would be rarely used...
        ctx.status(HttpStatus.METHOD_NOT_ALLOWED_405)
    }

    override fun delete(ctx: Context, resourceId: String) {
        val onSuccessStatement: (Any?) -> Unit = {
            ctx.status(204)
        }

        DeletePhotoUseCase(photoService)
                .execute(resourceId, onSuccessStatement) {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                }
    }
}

class PhotoDtoMapper {
    fun mapModelToDto(photo: Photo): PhotoDTO = PhotoDTO(photo.bytes, photo.tags, photo.originalUrl)

    fun mapDtoToModel(photoDto: PhotoDTO): Photo = Photo(photoDto.bytes, photoDto.tags, photoDto.originalUrl, "")
}