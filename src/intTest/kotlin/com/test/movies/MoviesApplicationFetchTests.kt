package com.test.movies

import com.test.movies.dto.MovieDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.returnResult
import java.time.LocalDate

class MoviesApplicationFetchTests: MoviesApplicationTests() {

    @Test
    fun `fetch on empty database should return empty list`() {
        getAll()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(APPLICATION_JSON)
            .expectBody()
            .json("[]")
    }

    @Test
    fun `fetch with wrong id should return 404`() {
        getOne("/v1/movies/1")
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `fetch by id should return correct object`() {
        val savedDto = create(MovieDto("title1", LocalDate.now(), stars = setOf("star1")))
            .returnResult<MovieDto>()
            .responseBody
            .blockFirst() ?: fail("Created object wasn't returned")

        getOne("/v1/movies/${savedDto.id}")
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.title").isEqualTo("title1")
    }
}
