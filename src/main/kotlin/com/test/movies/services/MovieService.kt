package com.test.movies.services

import com.test.movies.dto.MovieDto

interface MovieService {
    fun findAll(): Set<MovieDto>

    fun create(movieDto: MovieDto): MovieDto

    fun delete(id: Long)

    fun update(id: Long, movie: MovieDto): MovieDto

    fun findById(id: Long): MovieDto
}
