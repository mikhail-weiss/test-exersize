package com.test.movies

import com.test.movies.dto.MovieDto
import org.assertj.core.api.Assertions.fail
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.reactive.server.returnResult
import java.time.LocalDate

class MoviesApplicationUpdateTests : MoviesApplicationTests() {

    @Test
    fun `movie update with wrong id should return 404`() {
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))
        update(movieDto, "/v1/movies/1").expectStatus().isNotFound
    }

    @Test
    fun `movie update should update title be successfully`() {
        val savedMovieDto = getSavedMovieDto()

        getAll()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].title", hasSize<Any>(1)).isEqualTo("title1")

        update(
            MovieDto("title2", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2")),
            "/v1/movies/${savedMovieDto.id}"
        )
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("title2")

        getAll()
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
        val savedMovieDto: MovieDto = getSavedMovieDto()

        getAll()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].title").isEqualTo("title1")


        update(
            MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star2")),
            "/v1/movies/${savedMovieDto.id}")
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("title1")
            .jsonPath("$.stars").isEqualTo("star2")

        getAll()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].stars", hasSize<Any>(1)).isEqualTo("star2")
    }


    @Test
    fun `movie update with empty star list should return 400`() {
        val savedMovieDto: MovieDto = getSavedMovieDto()

        update(MovieDto("title1", LocalDate.now(), stars = setOf()), "/v1/movies/${savedMovieDto.id}")
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.error").isEqualTo("Bad Request")
    }

    @Test
    fun `movie update with empty title should return 400`() {
        val savedMovieDto: MovieDto = getSavedMovieDto()

        update(MovieDto("", LocalDate.now(), stars = setOf("star1")), "/v1/movies/${savedMovieDto.id}")
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.error").isEqualTo("Bad Request")
    }

    @Test
    fun `movie update with empty releaseDate should return 400`() {
        val savedMovieDto: MovieDto = getSavedMovieDto()

        webTestClient.put()
            .uri("/v1/movies/${savedMovieDto.id}")
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

    private fun getSavedMovieDto() =
        create(MovieDto("title1", LocalDate.now(), stars = setOf("star1")))
            .returnResult<MovieDto>()
            .responseBody.blockFirst() ?: fail("Created object wasn't returned")

}
