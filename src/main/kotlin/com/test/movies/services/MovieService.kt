package com.test.movies.services

import com.test.movies.dao.MovieDao
import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star
import org.springframework.stereotype.Service

@Service
class MovieService(val dao: MovieDao) {
    fun findAll(): List<MovieDto> {
        return dao.findAll().map { movie ->
            MovieDto(
                title = movie.title,
                releaseDate = movie.releaseDate,
                id = movie.id,
                stars = movie.stars.map { it.name }.toSet()
            )
        }
    }

    fun create(movie: MovieDto) = dao.save(
        Movie(
            title = movie.title,
            releaseDate = movie.releaseDate,
            stars = movie.stars.map { Star(it) }.toSet()
        )
    )

    fun delete(id: Long) {
        dao.deleteById(id)
    }
}
