package com.test.movies.services

import com.test.movies.dto.MovieDto
import com.test.movies.services.exceptions.MovieIsWrongException
import org.springframework.stereotype.Service

@Service
class MovieValidatorImpl : MovieValidator {
    override fun validateMovie(movie: MovieDto) {
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

    override fun validateStar(star: String) {
        if (star.isEmpty()) {
            throw MovieIsWrongException("All stars should be non-empty")
        }

        if (star.length >= 255) {
            throw MovieIsWrongException("Star name is too long")
        }
    }
}