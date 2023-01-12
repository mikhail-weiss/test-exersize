package com.test.movies.services

import com.test.movies.dto.MovieDto
import com.test.movies.services.exceptions.MovieIsWrongException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MovieValidatorImplTest {

    private val movieValidator: MovieValidator = MovieValidatorImpl()
    @Test
    fun `validate movie should allow correct movie`() {
        movieValidator.validateMovie(MovieDto("title", DEFAULT_LOCAL_DATE, setOf("star1", "star2")))
    }

    @Test
    fun `validate movie should fail movie with empty title`() {
        assertThrows<MovieIsWrongException> {
            movieValidator.validateMovie(MovieDto("", DEFAULT_LOCAL_DATE, setOf("star1", "star2")))
        }
    }

    @Test
    fun `validate movie should fail movie with too long title`() {
        assertThrows<MovieIsWrongException> {
            movieValidator.validateMovie(
                MovieDto(
                    "a".repeat(255),
                    DEFAULT_LOCAL_DATE,
                    setOf("star1", "star2")
                )
            )
        }
    }

    @Test
    fun `validate movie should fail movie without stars`() {

        assertThrows<MovieIsWrongException> {
            movieValidator.validateMovie(
                MovieDto(
                    "title",
                    DEFAULT_LOCAL_DATE,
                    setOf()
                )
            )
        }
    }

    @Test
    fun `validate movie should fail movie with too many stars`() {

        assertThrows<MovieIsWrongException> {
            movieValidator.validateMovie(
                MovieDto(
                    "title",
                    DEFAULT_LOCAL_DATE,
                    List(101) { i -> "star$i" }.toSet()
                )
            )
        }
    }

    @Test
    fun `validate movie should fail movie when star name is too long`() {

        assertThrows<MovieIsWrongException> {
            movieValidator.validateMovie(
                MovieDto(
                    "title",
                    DEFAULT_LOCAL_DATE,
                    setOf("a".repeat(255))
                )
            )
        }
    }

    @Test
    fun `validate movie should fail movie when star name is empty`() {
        assertThrows<MovieIsWrongException> {
            movieValidator.validateMovie(
                MovieDto(
                    "title",
                    DEFAULT_LOCAL_DATE,
                    setOf("")
                )
            )
        }
    }

}