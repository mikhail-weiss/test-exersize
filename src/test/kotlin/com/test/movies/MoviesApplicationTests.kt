package com.test.movies

import com.test.movies.dao.MovieDao
import com.test.movies.dto.MovieDto
import org.assertj.core.api.Assertions.fail
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import java.time.LocalDate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [MoviesApplication::class]
)
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
@AutoConfigureWebTestClient
class MoviesApplicationTests {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var movieDao: MovieDao

    @AfterEach
    fun tearDown() {
        movieDao.truncateAll();
    }

    @Test
    fun `empty database should return empty list`() {
        webTestClient.get()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .json("[]")
    }

    @Test
    fun `movie creation returns created movie with id`() {
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1"))

        webTestClient.post()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .bodyValue(movieDto)
            .exchange()
            .expectStatus()
            .isCreated
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.title").isEqualTo("title1")
            .jsonPath("$.releaseDate").isEqualTo("2000-03-07")
    }

    @Test
    fun `movie creation successful`() {
        val movieDto1 = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))
        val movieDto2 = MovieDto("title2", LocalDate.of(2000, 3, 7), stars = setOf("star1"))
        val movieDto3 = MovieDto("title3", LocalDate.of(2000, 3, 7), stars = setOf("star2"))

        webTestClient.post().uri("/v1/movies").bodyValue(movieDto1).exchange().expectStatus().isCreated
        webTestClient.post().uri("/v1/movies").bodyValue(movieDto2).exchange().expectStatus().isCreated
        webTestClient.post().uri("/v1/movies").bodyValue(movieDto3).exchange().expectStatus().isCreated

        webTestClient.get()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(3)).isArray
            .jsonPath("$.[0].stars", hasSize<Any>(2)).isArray
            .jsonPath("$.[1].stars", hasSize<Any>(1)).isArray
            .jsonPath("$.[2].stars", hasSize<Any>(1)).isArray
    }

    @Test
    fun `movie creation with empty star list should return 400`() {
        val movieDto = MovieDto("title1", LocalDate.now(), stars = setOf())

        webTestClient.post()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .bodyValue(movieDto)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.error").isEqualTo("Bad Request")
    }

    @Test
    fun `movie creation with empty title should return 400`() {
        val movieDto = MovieDto("", LocalDate.now(), stars = setOf("star1"))

        webTestClient.post()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .bodyValue(movieDto)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.error").isEqualTo("Bad Request")
    }

    @Test
    fun `movie creation with empty releaseDate should return 400`() {

        webTestClient.post()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .bodyValue("{\"title\": \"title\", \"stars\": [\"star1\"]}")
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.error").isEqualTo("Bad Request")
    }

    @Test
    fun `movie creation with existing title and releaseDate should return 400`() {
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1"))

        webTestClient.post()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .bodyValue(movieDto)
            .exchange()
            .expectStatus()
            .isCreated
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.title").isEqualTo("title1")
            .jsonPath("$.releaseDate").isEqualTo("2000-03-07")

        webTestClient.post()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .bodyValue(movieDto)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.error").isEqualTo("Bad Request")
    }

    @Test
    fun `movie deletion returns 204`() {
        val movieDto1 = MovieDto(
            "title1",
            LocalDate.of(2000, 3, 7),
            stars = setOf("star1", "star2")
        )

        val result = webTestClient.post()
            .uri("/v1/movies")
            .bodyValue(movieDto1)
            .exchange()
            .expectStatus().isCreated
            .returnResult<MovieDto>()
        val savedMovieDto: MovieDto? = result.getResponseBody().blockFirst()

        if (savedMovieDto == null) {
            fail<Any>("Didn't receive saved movie")
        }

        webTestClient.delete().uri("/v1/movies/${savedMovieDto?.id}").exchange().expectStatus().isNoContent
    }

    @Test
    fun `movie deletion doesnt remove related stars for other movies`() {
        val movieDto1 = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))
        val movieDto2 = MovieDto("title2", LocalDate.of(2000, 3, 7), stars = setOf("star1"))
        val movieDto3 = MovieDto("title3", LocalDate.of(2000, 3, 7), stars = setOf("star2"))

        val savedMovieDto: MovieDto? = webTestClient.post().uri("/v1/movies").bodyValue(movieDto1).exchange()
            .expectStatus().isCreated
            .returnResult<MovieDto>()
            .responseBody.blockFirst()

        if (savedMovieDto == null) {
            fail<Any>("Didn't receive saved movie")
        }

        webTestClient.post().uri("/v1/movies").bodyValue(movieDto2).exchange().expectStatus().isCreated
        webTestClient.post().uri("/v1/movies").bodyValue(movieDto3).exchange().expectStatus().isCreated

        webTestClient.delete().uri("/v1/movies/${savedMovieDto?.id}").exchange().expectStatus().isNoContent

        webTestClient.get()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].stars", hasSize<Any>(1)).isArray
            .jsonPath("$.[1].stars", hasSize<Any>(1)).isArray
    }

    @Test
    fun `movie deletion with wrong id should return 404`() {
        webTestClient.delete().uri("/v1/movies/1").exchange().expectStatus().isNotFound
    }

    @Test
    fun `movie update with wrong id should return 404`() {
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))

        webTestClient.put().uri("/v1/movies/1")
            .bodyValue(movieDto)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `movie update should update title be successfully`() {
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))
        val savedMovieDto: MovieDto? = webTestClient.post().uri("/v1/movies").bodyValue(movieDto).exchange()
            .expectStatus().isCreated
            .returnResult<MovieDto>()
            .responseBody.blockFirst()
        val newMovieDto = movieDto.copy(title = "title2")

        if (savedMovieDto == null) {
            fail<Any>("Couldn't save movie")
        }
        webTestClient.get()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].title", hasSize<Any>(1)).isEqualTo("title1")


        webTestClient.put().uri("/v1/movies/${savedMovieDto?.id}")
            .bodyValue(newMovieDto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("title2")

        webTestClient.get()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].title", hasSize<Any>(1)).isEqualTo("title2")
    }

    @Test
    fun `movie update should update star list successfully`() {
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))
        val savedMovieDto: MovieDto? = webTestClient.post().uri("/v1/movies").bodyValue(movieDto).exchange()
            .expectStatus().isCreated
            .returnResult<MovieDto>()
            .responseBody.blockFirst()
        val newMovieDto = movieDto.copy(stars = setOf("star3"))

        if (savedMovieDto == null) {
            fail<Any>("Couldn't save movie")
        }
        webTestClient.get()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].title").isEqualTo("title1")


        webTestClient.put().uri("/v1/movies/${savedMovieDto?.id}")
            .bodyValue(newMovieDto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("title1")

        webTestClient.get()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].stars", hasSize<Any>(1)).isEqualTo("star3")
    }
}
