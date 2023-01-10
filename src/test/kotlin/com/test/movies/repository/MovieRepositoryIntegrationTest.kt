package com.test.movies.repository

import com.test.movies.dao.MovieDao
import com.test.movies.dao.impl.MovieDaoImpl
import com.test.movies.model.Movie
import com.test.movies.model.Star
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate

@DataJpaTest
@Import(MovieDaoImpl::class)
class MovieRepositoryIntegrationTest(
    @Autowired
    val movieRepository: MovieRepository,
    @Autowired
    val starRepository: StarRepository,
    @Autowired
    val movieDao: MovieDao
) {
    @Test
    fun `saving a movie should save a movie`() {
        val expectedStars = { setOf(Star("star1"), Star("star2")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        val savedMovie = movieDao.save(movie)
        val actualMovie = savedMovie.id?.let { movieDao.getById(it) }?.get()

        assertThat(actualMovie?.releaseDate, equalTo(movie.releaseDate))
        assertThat(actualMovie?.title, equalTo(movie.title))
        assertThat(actualMovie?.stars?.toTypedArray(), equalTo(movie.stars.toTypedArray()))
    }

    @Test
    fun `saving a movie should save new stars`() {
        val expectedStars = { setOf(Star("star1"), Star("star2")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        movieDao.save(movie)

        val actualStars = starRepository.findAll().toSet()

        assertThat(actualStars.map { it.name }, equalTo(expectedStars().map { it.name }))
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

        movieDao.save(movie)

        actualStars = starRepository.findAll()
        assertThat(actualStars.size, equalTo(1))
        assertThat(actualStars[0].name, equalTo(expectedStars().toList()[0].name))
    }

    @Test
    fun `saving same movie twice should fail`() {
        val expectedStars = { setOf(Star("star1"), Star("star2")) }
        val movie = Movie("test title", LocalDate.now(), expectedStars())
        movieDao.save(movie)

        try {
            movieDao.save(movie)
            movieRepository.flush()
            assertThat("Exception should have been thrown", false)
        } catch(ex: DataIntegrityViolationException) {
            //
        }
    }
}