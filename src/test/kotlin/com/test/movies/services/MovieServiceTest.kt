package com.test.movies.services

import com.test.movies.services.exceptions.MovieIsWrongException
import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class MovieServiceTest {

    private val movieService: MovieService = MovieService(mockk(), mockk())

    @Nested
    inner class TestDtoTransformation {

        @Test
        fun `transform to dto`() {

            val releaseDate = LocalDate.of(2000, 1, 1)
            val title = "title"
            val stars = setOf(Star("star1"), Star("star2"))
            val movie = Movie(title, releaseDate, stars).apply { id = 1L }
            val actualDto = movieService.toDto(movie)

            assertThat(actualDto.id, equalTo(1L))
            assertThat(actualDto.title, equalTo(title))
            assertThat(actualDto.releaseDate, equalTo(releaseDate))
            assertThat(actualDto.stars, equalTo(stars.map { it.name }.toSet()))
        }

        @Test
        fun `transform from model without id should work`() {
            val releaseDate = LocalDate.of(2000, 1, 1)
            val title = "title"
            val stars = setOf(Star("star1"), Star("star2"))
            val movie = Movie(title, releaseDate, stars)
            val actualDto = movieService.toDto(movie)

            assertThat(actualDto.id, nullValue())
            assertThat(actualDto.title, equalTo(title))
            assertThat(actualDto.releaseDate, equalTo(releaseDate))
            assertThat(actualDto.stars, equalTo(stars.map { it.name }.toSet()))
        }

        @Test
        fun `transform to model should work`() {
            val releaseDate = LocalDate.of(2000, 1, 1)
            val title = "title"
            val stars = setOf("star1", "star2")
            val movie = MovieDto(
                title = title,
                releaseDate = releaseDate,
                stars = stars
            )
            val actualDto = movieService.fromDto(movie)

            assertThat(actualDto.id, nullValue())
            assertThat(actualDto.title, equalTo(title))
            assertThat(actualDto.releaseDate, equalTo(releaseDate))
            assertThat(actualDto.stars, equalTo(stars.map { Star(it) }.toSet()))
        }

        @Test
        fun `transform to model should pass id correctly`() {
            val releaseDate = LocalDate.of(2000, 1, 1)
            val title = "title"
            val stars = setOf("star1", "star2")
            val movie = MovieDto(
                title = title,
                releaseDate = releaseDate,
                id = 1L,
                stars = stars
            )
            val actualDto = movieService.fromDto(movie)

            assertThat(actualDto.id, equalTo(1L))
            assertThat(actualDto.title, equalTo(title))
            assertThat(actualDto.releaseDate, equalTo(releaseDate))
            assertThat(actualDto.stars, equalTo(stars.map { Star(it) }.toSet()))
        }

    }

    @Nested
    inner class TestValidation {

        @Test
        fun `validate movie should allow correct movie`() {
            val movie = MovieDto(
                title = "title",
                releaseDate = LocalDate.of(2000, 1, 1),
                stars = setOf("star1", "star2")
            )

            try {
                movieService.validateMovie(movie)
            } catch (e: Exception) {
                fail(e.message)
            }
        }

        @Test
        fun `validate movie should fail movie with empty title`() {
            val movie = MovieDto(
                title = "",
                releaseDate = LocalDate.of(2000, 1, 1),
                stars = setOf("star1", "star2")
            )

            try {
                movieService.validateMovie(movie)
                fail("should fail movie with empty title")
            } catch (_: Exception) {
            }
        }

        @Test
        fun `validate movie should fail movie with too long title`() {
            val movie = MovieDto(
                title = "a".repeat(255),
                releaseDate = LocalDate.of(2000, 1, 1),
                stars = setOf("star1", "star2")
            )

            try {
                movieService.validateMovie(movie)
                fail("should fail movie with too long title message")
            } catch (_: Exception) {
            }
        }

        @Test
        fun `validate movie should fail movie without stars`() {
            val movie = MovieDto(
                title = "title",
                releaseDate = LocalDate.of(2000, 1, 1),
                stars = setOf()
            )

            try {
                movieService.validateMovie(movie)
                fail("should fail movie without star")
            } catch (_: MovieIsWrongException) {
            }
        }

        @Test
        fun `validate movie should fail movie with too many stars`() {
            val movie = MovieDto(
                title = "title",
                releaseDate = LocalDate.of(2000, 1, 1),
                stars = List(101) { i -> "star$i" }.toSet()
            )

            try {
                movieService.validateMovie(movie)
                fail("should fail movie with too many stars")
            } catch (_: MovieIsWrongException) {
            }
        }

        @Test
        fun `validate movie should fail movie when star name is too long`() {
            val movie = MovieDto(
                title = "title",
                releaseDate = LocalDate.of(2000, 1, 1),
                stars = setOf("a".repeat(255))
            )

            try {
                movieService.validateMovie(movie)
                fail("should fail movie when star name is too long")
            } catch (_: MovieIsWrongException) {
            }
        }

        @Test
        fun `validate movie should fail movie when star name is empty`() {
            val movie = MovieDto(
                title = "title",
                releaseDate = LocalDate.of(2000, 1, 1),
                stars = setOf("")
            )

            try {
                movieService.validateMovie(movie)
                fail("should fail movie when star name is too long")
            } catch (_: MovieIsWrongException) {
            }
        }
    }

}