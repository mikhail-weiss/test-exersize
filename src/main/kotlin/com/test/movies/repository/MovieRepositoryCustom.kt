package com.test.movies.repository

import com.test.movies.model.Movie

interface MovieRepositoryCustom {
    /**
     * Saves movie.
     * Ensures that related stars in star list are created only if necessary.
     */
    fun saveFull(movie: Movie): Movie
}