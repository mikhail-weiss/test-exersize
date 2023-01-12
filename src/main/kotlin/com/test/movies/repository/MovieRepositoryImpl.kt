package com.test.movies.repository

import com.test.movies.model.Movie
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Lazy

open class MovieRepositoryImpl(
    @Lazy private val movieRepository: MovieRepository,
    private val starRepository: StarRepository
) : MovieRepositoryCustom {

    @Transactional
    override fun saveFull(movie: Movie): Movie {
        movie.stars = movie.stars.map {
            this.starRepository.findByName(it.name) ?: it
        }.toSet()
        return movieRepository.save(movie)
    }
}