package com.test.movies

import com.test.movies.dto.MovieDto
import org.assertj.core.api.Assertions.fail
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.returnResult
import java.time.LocalDate

class MoviesApplicationDeleteTests : MoviesApplicationTests() {
    @Test
    fun `movie deletion returns 204`() {
        val movieDto1 = MovieDto(
            "title1",
            LocalDate.of(2000, 3, 7),
            stars = setOf("star1", "star2")
        )

        val savedMovieDto = create(movieDto1)
            .expectStatus().isCreated
            .returnResult<MovieDto>()
            .responseBody
            .blockFirst() ?: fail("Created object wasn't returned")

        delete("/v1/movies/${savedMovieDto?.id}").expectStatus().isNoContent
    }

    @Test
    fun `movie deletion doesnt remove related stars for other movies`() {
        val movieDto1 = MovieDto("title1", LocalDate.now(), stars = setOf("star1", "star2"))
        val movieDto2 = MovieDto("title2", LocalDate.now(), stars = setOf("star1"))
        val movieDto3 = MovieDto("title3", LocalDate.now(), stars = setOf("star2"))

        val savedMovieDto: MovieDto? = create(movieDto1)
            .expectStatus().isCreated
            .returnResult<MovieDto>()
            .responseBody.blockFirst() ?: fail("Created object wasn't returned")

        create(movieDto2).expectStatus().isCreated
        create(movieDto3).expectStatus().isCreated

        delete("/v1/movies/${savedMovieDto?.id}").expectStatus().isNoContent

        getAll()
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
        delete("/v1/movies/1").expectStatus().isNotFound
    }
}
