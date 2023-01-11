package com.test.movies.services

import com.test.movies.services.exceptions.MovieAlreadyExistsException
import com.test.movies.services.exceptions.MovieIsWrongException
import com.test.movies.services.exceptions.MovieNotFoundException
import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star
import com.test.movies.repository.MovieRepository
import com.test.movies.repository.StarRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
class MovieService(private val movieRepository: MovieRepository) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun toDto(movie: Movie): MovieDto =
        MovieDto(
            title = movie.title,
            releaseDate = movie.releaseDate,
            id = movie.id,
            stars = movie.stars.map { it.name }.toSet()
        )

    fun fromDto(movieDto: MovieDto): Movie =
        Movie(
            title = movieDto.title,
            releaseDate = movieDto.releaseDate,
            stars = movieDto.stars.map { Star(it) }.toSet()
        ).apply {
            id = movieDto.id
        }

    fun findAll(): List<MovieDto> {
        return this.movieRepository.findAll().map { toDto(it) }
    }

    fun create(movieDto: MovieDto): MovieDto {
        validateMovie(movieDto)

        try {
            val movie = fromDto(movieDto)

            return toDto(movieRepository.saveFull(movie))
        } catch (e: DataIntegrityViolationException) {
            log.error("Error creating the movie", e)

            throw MovieAlreadyExistsException()
        }
    }

    fun validateMovie(movie: MovieDto) {
        if (movie.title.length >= 255) {
            throw MovieIsWrongException("Title is too long")
        }

        if (movie.stars.isEmpty()) {
            throw MovieIsWrongException("There must be at least one entry in the stars list")
        }

        if (movie.stars.size >= 100) {
            throw MovieIsWrongException("Star list is too big")
        }

        movie.stars.forEach { validateStar(it) }

        if (movie.title.isEmpty()) {
            throw MovieIsWrongException("Title shouldn't be empty")
        }
    }

    private fun validateStar(star: String) {
        if (star.isEmpty()) {
            throw MovieIsWrongException("All stars should be non-empty")
        }

        if (star.length >= 255) {
            throw MovieIsWrongException("Star name is too long")
        }
    }

    fun delete(id: Long) {
        try {
            movieRepository.deleteById(id)
        } catch (e: EmptyResultDataAccessException) {
            log.error("Couldn't find an element to delete", e)
            throw MovieNotFoundException()
        }
    }

    fun update(id: Long, movie: MovieDto): MovieDto {
        validateMovie(movie)
        if (!movieRepository.existsById(id)) {
            throw MovieNotFoundException()
        }
        return toDto(movieRepository.saveFull(fromDto(movie.copy(id = id))))
    }

    fun findById(id: Long): MovieDto {
        val movieOptional = this.movieRepository.findById(id)
        return toDto(movieOptional.orElseThrow { MovieNotFoundException() })
    }
}
