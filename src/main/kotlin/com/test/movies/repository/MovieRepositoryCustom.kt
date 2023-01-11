package com.test.movies.repository

import com.test.movies.model.Movie

interface MovieRepositoryCustom {
    fun saveFull(movie: Movie): Movie
}