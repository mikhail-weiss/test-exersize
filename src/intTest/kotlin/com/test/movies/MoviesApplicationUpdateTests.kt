package com.test.movies

import com.test.movies.dto.MovieDto
import org.assertj.core.api.Assertions.fail
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
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
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))

        val savedMovieDto: MovieDto? = create(movieDto)
            .expectStatus().isCreated
            .returnResult<MovieDto>()
            .responseBody.blockFirst()  ?: fail("Created object wasn't returned")
        val newMovieDto = movieDto.copy(title = "title2")

        getAll()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].title", hasSize<Any>(1)).isEqualTo("title1")

        update(newMovieDto, "/v1/movies/${savedMovieDto?.id}")
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
        val movieDto = MovieDto("title1", LocalDate.of(2000, 3, 7), stars = setOf("star1", "star2"))
        val savedMovieDto: MovieDto? = create(movieDto)
            .returnResult<MovieDto>()
            .responseBody.blockFirst()  ?: fail("Created object wasn't returned")
        val newMovieDto = movieDto.copy(stars = setOf("star3"))

        getAll()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].title").isEqualTo("title1")


        update(newMovieDto, "/v1/movies/${savedMovieDto?.id}")
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("title1")

        getAll()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$", hasSize<Any>(2)).isArray
            .jsonPath("$.[0].stars", hasSize<Any>(1)).isEqualTo("star3")
    }



}
