package com.test.movies.dao

import com.test.movies.model.Movie
import java.util.*

interface MovieDao {
    fun save(movie: Movie): Movie
    fun findAll(): MutableList<Movie>
    fun deleteById(id: Long)
    fun getById(id: Long): Optional<Movie>

    /**
     * Removes all data from every table
     */
    fun truncateAll()
    fun exists(id: Long): Boolean
}