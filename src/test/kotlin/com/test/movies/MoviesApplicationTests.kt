package com.test.movies

import com.test.movies.controllers.MovieController
import com.test.movies.dao.MovieDao
import com.test.movies.services.MovieService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MoviesApplicationTests {

    @Autowired
    lateinit var controller: MovieController;
    @Autowired
    lateinit var movieService: MovieService
    @Autowired
    lateinit var movieDao: MovieDao

    @Test
    fun contextLoads() {
        assertThat(controller).isNotNull;
        assertThat(movieService).isNotNull;
        assertThat(movieDao).isNotNull;

    }

}
