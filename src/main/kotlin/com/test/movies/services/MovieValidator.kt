package com.test.movies.services

import com.test.movies.dto.MovieDto

interface MovieValidator {
    fun validateMovie(movie: MovieDto)
    fun validateStar(star: String)
}