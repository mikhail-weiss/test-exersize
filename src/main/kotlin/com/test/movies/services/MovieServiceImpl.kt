package com.test.movies.services

import com.test.movies.dto.MovieDto
import com.test.movies.repository.MovieRepository
import com.test.movies.services.exceptions.MovieAlreadyExistsException
import com.test.movies.services.exceptions.MovieNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
class MovieServiceImpl (
    private val movieRepository: MovieRepository,
    private val movieValidator: MovieValidator
): MovieService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun findAll(): Set<MovieDto> {
        return this.movieRepository.findAll().map { toDto(it) }.toSet()
    }

    override fun create(movieDto: MovieDto): MovieDto {
        movieValidator.validateMovie(movieDto)

        try {
            val movie = fromDto(movieDto)

            return toDto(movieRepository.saveFull(movie))
        } catch (e: DataIntegrityViolationException) {
            log.error("Error creating the movie", e)

            throw MovieAlreadyExistsException()
        }
    }

    override fun delete(id: Long) {
        try {
            movieRepository.deleteById(id)
        } catch (e: EmptyResultDataAccessException) {
            log.error("Couldn't find an element to delete", e)
            throw MovieNotFoundException()
        }
    }

    override fun update(id: Long, movie: MovieDto): MovieDto {
        movieValidator.validateMovie(movie)
        if (!movieRepository.existsById(id)) {
            throw MovieNotFoundException()
        }
        return toDto(movieRepository.saveFull(fromDto(movie.copy(id = id))))
    }

    override fun findById(id: Long): MovieDto {
        val movieOptional = this.movieRepository.findById(id)
        return toDto(movieOptional.orElseThrow { MovieNotFoundException() })
    }
}
