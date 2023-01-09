package com.test.movies.services

import com.test.movies.dao.MovieDao
import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star
import org.springframework.stereotype.Service

@Service
class MovieService(val dao: MovieDao) {

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
        return dao.findAll().map { toDto(it) }
    }

    fun create(movie: MovieDto): MovieDto {
        if (movie.stars.isEmpty()) {
            throw IllegalArgumentException("There must be at least one entry in the stars list")
        }
        return toDto(dao.save(fromDto(movie)))
    }

    fun delete(id: Long) {
        dao.deleteById(id)
    }
}
