package com.phototagger.datalake.rest

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.phototagger.datalake.*
import com.phototagger.datalake.model.Photo
import io.javalin.Javalin
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

const val PORT = 7001
const val PATH_PHOTOS = "/photos"
const val MIME_TYPE_JSON = "application/json"

@Tag("rest")
class RestTest {

    private lateinit var service: PhotoService
    private lateinit var controller: RestController
    private lateinit var server: Javalin

    init {
        FuelManager.instance.basePath = "http://localhost:$PORT"
    }

    @BeforeEach
    internal fun setUp() {
        service = mockk()
        controller = RestController(PORT, service)
        server = controller.createServer()
    }

    @AfterEach
    internal fun tearDown() {
        server.stop()
    }

    //region POST
    @Test
    fun `POST responds with 201 on success`() {
        val photo = PhotoDTO(byteArrayOf(1, 2), listOf("tag1", "tag2"), "example.com")
        every { service.savePhoto(any()) } returns "1"

        val (_, response, _) = Fuel.post(PATH_PHOTOS)
                .body(PhotoSerializer().serialize(photo))
                .response()

        assertEquals(201, response.statusCode)
    }

    @Test
    fun `POST succeeds with Location in response headers`() {
        val photo = PhotoDTO(byteArrayOf(1, 2), listOf("tag1", "tag2"), "example.com")

        every { service.savePhoto(any()) } returns "1"

        val (_, response, _) = Fuel.post(PATH_PHOTOS)
                .body(PhotoSerializer().serialize(photo))
                .response()

        val location = response.headers["Location"]
        assertEquals("http://localhost:$PORT/photos/1", location?.firstOrNull())
    }

    @Test
    fun `POST succeeds with no content in response`() {
        val photo = PhotoDTO(byteArrayOf(1, 2), listOf("tag1", "tag2"), "example.com")

        every { service.savePhoto(any()) } returns "1"

        val (_, response, _) = Fuel.post(PATH_PHOTOS)
                .body(PhotoSerializer().serialize(photo).toByteArray())
                .response()

        assertEquals(201, response.statusCode)
        assert(response.data.isEmpty())
    }
    //endregion


    //region GET
    @Test
    fun `GET All responds with 200`() {
        every { service.getAll() } returns emptyList()

        val (_, response, _) = Fuel.get(PATH_PHOTOS).response()

        assertEquals(200, response.statusCode)
    }

    @Test
    fun `GET All responds with JSON content type`() {
        val (_, response, _) = Fuel.get(PATH_PHOTOS).response()

        assertEquals(MIME_TYPE_JSON, response.headers["Content-Type"]?.firstOrNull())
    }

    @Test
    fun `GET All responds with list of urls`() {
        every { service.getAll() } returns listOf("1", "2", "3")

        val (_, response, result) = Fuel.get(PATH_PHOTOS).responseObject(object : ResponseDeserializable<JsonArray> {
            override fun deserialize(content: String): JsonArray? {
                return Gson().fromJson(content, JsonArray::class.java)
            }
        })

        assertEquals(200, response.statusCode)

        val (jsonArray, _) = result
        val urls = jsonArray?.map { it.asString }

        assertArrayEquals(urls?.toTypedArray(), arrayOf(
                "http://localhost:$PORT/photos/1",
                "http://localhost:$PORT/photos/2",
                "http://localhost:$PORT/photos/3"))
    }

    @Test
    fun `GET Existing responds with 200`() {
        every { service.getPhoto("1") } returns Photo(byteArrayOf(), listOf("tag1", "tag2"), "example.com", "/tmp/1")

        val (_, response, _) = Fuel.get("$PATH_PHOTOS/1").response()

        assertEquals(200, response.statusCode)
    }

    @Test
    fun `GET Existing responds with JSON content type`() {
        every { service.getPhoto("1") } returns Photo(byteArrayOf(), listOf("tag1", "tag2"), "example.com", "/tmp/1")

        val (_, response, _) = Fuel.get("$PATH_PHOTOS/1").response()

        assertEquals(MIME_TYPE_JSON, response.headers["Content-Type"]?.firstOrNull())
    }

    @Test
    fun `GET Existing responds with content`() {
        every { service.getPhoto("1") } returns Photo(byteArrayOf(1, 2), listOf("tag1", "tag2"), "example.com", "/tmp/1")

        val (request, response, result) = Fuel.get("$PATH_PHOTOS/1").responseObject(TestPhotoDeserializer())

        println(request)
        println(response)
        println(result)

        val (photoDto, _) = result
        println(photoDto)

        photoDto?.let {
            assertArrayEquals(byteArrayOf(1, 2), photoDto.bytes)
            assertArrayEquals(arrayOf("tag1", "tag2"), photoDto.tags.toTypedArray())
            assertEquals("example.com", photoDto.originalUrl)
        }

        assertNotNull(photoDto)
    }

    @Test
    fun `GET Nonexisting responds with 404`() {
        every { service.getPhoto("1") } returns null

        val (_, response, _) = Fuel.get("$PATH_PHOTOS/1").response()

        assertEquals(404, response.statusCode)
    }
    //endregion


    //region DELETE
    @Test
    fun deleteExisting_respondsWith204() {
        every { service.delete(any()) } just Runs
        val (_, response, _) = Fuel.delete("$PATH_PHOTOS/1").response()
        assertEquals(204, response.statusCode)
    }
    //endregion
}

class TestPhotoDeserializer : ResponseDeserializable<PhotoDTO> {
    override fun deserialize(content: String): PhotoDTO? = PhotoDeserializer().deserialize(content)
}