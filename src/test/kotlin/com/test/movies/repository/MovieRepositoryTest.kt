package com.test.movies.repository

import com.test.movies.model.Movie
import com.test.movies.model.Star
import com.test.movies.services.MovieService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate

@DataJpaTest
@Import(MovieService::class)
class MovieRepositoryIntegrationTest(
    @Autowired
    val starRepository: StarRepository,
    @Autowired
    val movieRepository: MovieRepository
) {
    @Test
    fun `saving a movie should save a movie`() {
        val expectedStars = { setOf(Star("star1"), Star("star2")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        val savedMovie = movieRepository.saveFull(movie)
        val actualMovie = savedMovie.id?.let { movieRepository.findById(it) }?.get()

        assertThat(actualMovie?.releaseDate, equalTo(movie.releaseDate))
        assertThat(actualMovie?.title, equalTo(movie.title))
        assertThat(actualMovie?.stars?.toTypedArray(), equalTo(movie.stars.toTypedArray()))
    }

    @Test
    fun `saving a movie should save new stars`() {
        val expectedStars = { setOf(Star("star1"), Star("star2")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        movieRepository.saveFull(movie)

        val actualStars = starRepository.findAll().toSet()

        assertThat(actualStars.map { it.name }, equalTo(expectedStars().map { it.name }))
    }

    @Test
    fun `saving same movie twice should fail`() {
        val movie = { Movie("test title", LocalDate.now(), setOf(Star("star1"), Star("star2"))) }
        movieRepository.saveFull(movie())

        try {
            movieRepository.saveFull(movie())
            movieRepository.flush()
            assertThat("Exception should have been thrown", false)
        } catch (_: DataIntegrityViolationException) { }
    }

    @Test
    fun `deleting movie should not delete its related stars`() {
        val expectedStars = { setOf(Star("star1"), Star("star2")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        val savedMovie = movieRepository.saveFull(movie)

        if (savedMovie.id != null) {
            movieRepository.deleteById(savedMovie.id!!)
            assertThat(starRepository.findAll(), hasSize(2))
        } else {
            fail("Didnt receive saved object")
        }
    }

    @Test
    fun `updating movie stars should create new stars`() {
        val expectedStars = { setOf(Star("star1"), Star("star2")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        val savedMovie = movieRepository.saveFull(movie)

        if (savedMovie.id != null) {
            savedMovie.stars = setOf(Star("star3"), Star("star4"))
            movieRepository.saveFull(savedMovie)
            assertThat(starRepository.findAll(), hasSize(4))
        } else {
            fail("Didnt receive saved object")
        }
    }


    @Test
    fun `saving a movie should not create duplicate stars`() {
        val expectedStars = { setOf(Star("star1")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        assertThat("", starRepository.findAll().size, equalTo(0))

        starRepository.saveAll(expectedStars())
        var actualStars = starRepository.findAll()

        assertThat(starRepository.findAll().size, equalTo(1))
        assertThat(actualStars[0].name, equalTo(expectedStars().toList()[0].name))

        movieRepository.saveFull(movie)

        actualStars = starRepository.findAll()
        assertThat(actualStars.size, equalTo(1))
        assertThat(actualStars[0].name, equalTo(expectedStars().toList()[0].name))
    }
}