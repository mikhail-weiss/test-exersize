package com.test.movies.dao.impl

import com.test.movies.dao.MovieDao
import com.test.movies.model.Movie
import com.test.movies.repository.MovieRepository
import com.test.movies.repository.StarRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class MovieDaoImpl(private val movieRepository: MovieRepository, private val starRepository: StarRepository) :
    MovieDao {

    @Transactional
    override fun save(movie: Movie): Movie {
        movie.stars = movie.stars.map {
            this.starRepository.findByName(it.name) ?: it
        }.toSet()
        return this.movieRepository.save(movie)
    }

    override fun findAll(): MutableList<Movie> {
        return this.movieRepository.findAll()
    }

    override fun deleteById(id: Long) {
        this.movieRepository.deleteById(id)
    }

    override fun getById(id: Long): Optional<Movie> {
        return this.movieRepository.findById(id)

    }
}