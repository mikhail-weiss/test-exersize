package com.test.movies.services

import com.test.movies.dao.MovieDao
import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class MovieServiceTest {

    @MockK
    lateinit var movieDao: MovieDao

    @Test
    fun `transform to dto`() {
        val movieService = MovieService(movieDao)
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
        val movieService = MovieService(movieDao)
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
        val movieService = MovieService(movieDao)
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
        val movieService = MovieService(movieDao)
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