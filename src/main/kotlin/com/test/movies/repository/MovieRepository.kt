package com.test.movies.repository

import com.test.movies.model.Movie
import org.springframework.data.jpa.repository.JpaRepository

interface MovieRepository : JpaRepository<Movie, Long>, MovieRepositoryCustom
