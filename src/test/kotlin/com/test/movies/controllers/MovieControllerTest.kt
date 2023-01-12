package com.test.movies.controllers


import com.fasterxml.jackson.databind.ObjectMapper
import com.test.movies.dto.MovieDto
import com.test.movies.services.DEFAULT_LOCAL_DATE
import com.test.movies.services.MovieService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(MovieController::class)
class MovieControllerTest {

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun service() = mockk<MovieService>()
    }

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var service: MovieService

    @Test
    fun `get should return json array`() {
        every { service.findAll() } returns setOf(
            MovieDto("movie1", DEFAULT_LOCAL_DATE, stars = setOf()),
            MovieDto("movie2", DEFAULT_LOCAL_DATE, stars = setOf())
        )

        mvc.perform(
            get("/v1/movies")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].title", equalTo("movie1")))
            .andExpect(jsonPath("$[1].title", equalTo("movie2")))

        verify { service.findAll() }
    }

    @Test
    fun `post should create and then return created movie with an id`() {
        val movie = MovieDto("movie1", DEFAULT_LOCAL_DATE, stars = setOf("star1"))

        every { service.create(any()) } returns movie.copy(id = 1)

        mvc.perform(
            post("/v1/movies")
                .content(objectMapper.writeValueAsString(movie))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title", equalTo(movie.title)))
            .andExpect(jsonPath("$.id", equalTo(1)))

        verify { service.create(MovieDto("movie1", DEFAULT_LOCAL_DATE, setOf("star1"))) }
    }

    @Test
    fun `put should update and then return updated movie with an id`() {
        val movie = MovieDto("movie1", DEFAULT_LOCAL_DATE, stars = setOf("star1"))

        every { service.update(any(), any()) } returns movie.copy(id = 1)

        mvc.perform(
            put("/v1/movies/1")
                .content(objectMapper.writeValueAsString(movie))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title", equalTo(movie.title)))
            .andExpect(jsonPath("$.id", equalTo(1)))

        verify { service.update(1, movie) }
    }

    @Test
    fun `getById should return one object`() {
        every { service.findById(any()) } returns MovieDto("movie1", DEFAULT_LOCAL_DATE, setOf("star1"), 1)

        mvc.perform(get("/v1/movies/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title", equalTo("movie1")))
            .andExpect(jsonPath("$.id", equalTo(1)))

        verify { service.findById(1) }
    }

    @Test
    fun `delete should call delete service with correct id`() {
        every { service.delete(1) } returns Unit
        mvc.perform(
            delete("/v1/movies/1")
        )
            .andExpect(status().isNoContent)

        verify { service.delete(1) }
    }

}