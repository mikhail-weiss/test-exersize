package com.test.movies

import com.test.movies.dto.MovieDto
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.time.LocalDate

class MoviesApplicationCreateTests : MoviesApplicationTests() {

    @Test
    fun `movie creation returns created movie with id`() {
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1"))

        create(movieDto)
            .expectStatus().isCreated
            .expectHeader().contentType(APPLICATION_JSON)
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

        create(movieDto1).expectStatus().isCreated
        create(movieDto2).expectStatus().isCreated
        create(movieDto3).expectStatus().isCreated

        getAll()
            .expectBody()
            .jsonPath("$", hasSize<Any>(3)).isArray
            .jsonPath("$.[0].stars", hasSize<Any>(2)).isArray
            .jsonPath("$.[1].stars", hasSize<Any>(1)).isArray
            .jsonPath("$.[2].stars", hasSize<Any>(1)).isArray
    }

    @Test
    fun `movie creation with empty star list should return 400`() {
        val movieDto = MovieDto("title1", LocalDate.now(), stars = setOf())

        create(movieDto)
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

        create(movieDto)
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

        create(movieDto)
            .expectStatus().isCreated
            .expectHeader().contentType(APPLICATION_JSON)
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.title").isEqualTo("title1")
            .jsonPath("$.releaseDate").isEqualTo("2000-03-07")

        create(movieDto)
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isMap
            .jsonPath("$.error").isEqualTo("Bad Request")
    }
}
