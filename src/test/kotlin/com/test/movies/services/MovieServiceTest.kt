package com.test.movies.services

import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star
import com.test.movies.repository.MovieRepository
import com.test.movies.services.exceptions.MovieNotFoundException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

val DEFAULT_LOCAL_DATE: LocalDate = LocalDate.of(2000, 1, 1)

@ExtendWith(MockKExtension::class)
class MovieServiceTest(@MockK private var movieRepository: MovieRepository) {
    private val movieService: MovieService = MovieServiceImpl(movieRepository, mockk())

    @Test
    fun `findById should throw movienotfound exception if movie doesnt exist`() {
        every { movieRepository.findById(1) } returns Optional.empty()
        assertThrows<MovieNotFoundException> { movieService.findById(1) }
    }

    @Test
    fun `findById should return correct dto`() {
        every { movieRepository.findById(1) } returns
                Optional.of(Movie("title1", DEFAULT_LOCAL_DATE, setOf(Star("star1"))))

        assertEquals(
            MovieDto("title1", DEFAULT_LOCAL_DATE, setOf("star1")),
            movieService.findById(1)
        )
    }

    @Test
    fun `findAll should return correct objects with ids exist`() {
        every { movieRepository.findAll() } returns
                mutableListOf(
                    Movie("title1", DEFAULT_LOCAL_DATE, setOf(Star("star1"))),
                    Movie("title2", DEFAULT_LOCAL_DATE, setOf(Star("star2")))
                )
        assertEquals(
            movieService.findAll(), setOf(
                MovieDto("title1", DEFAULT_LOCAL_DATE, setOf("star1")),
                MovieDto("title2", DEFAULT_LOCAL_DATE, setOf("star2"))
            )
        )
    }
}