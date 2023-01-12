package com.test.movies.services

import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star

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

