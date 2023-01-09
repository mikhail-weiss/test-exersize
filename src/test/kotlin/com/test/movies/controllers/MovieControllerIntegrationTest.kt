package com.test.movies.controllers


import com.fasterxml.jackson.databind.ObjectMapper
import com.test.movies.dto.MovieDto
import com.test.movies.services.MovieService
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(MovieController::class)
class MovieControllerIntegrationTest {

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

        val movie = MovieDto("movie1", LocalDate.now(), stars = setOf());

        every { service.findAll() } returns listOf(movie)

        mvc.perform(
            get("/v1/movies")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].title", equalTo(movie.title)))

    }


    @Test
    fun `post should create and then return created movie with an id`() {

        val movie = MovieDto("movie1", LocalDate.now(), stars = setOf("star1"));

        every { service.findAll() } returns listOf(movie)
        every { service.create(any()) } returns movie.copy(id = 1)

        mvc.perform(
            post("/v1/movies")
                .content(objectMapper.writeValueAsString(movie))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title", equalTo(movie.title)))
            .andExpect(jsonPath("$.id", equalTo(1)))
    }

    @Test
    fun `post should fail with 400 if no stars provided`() {

        val movie = MovieDto("movie1", LocalDate.now(), stars = setOf());

        every { service.findAll() } returns listOf(movie)
        every { service.create(any()) } returns movie.copy(id = 1)

        val thrown = assertThrows<jakarta.servlet.ServletException> {

            mvc.perform(
                post("/v1/movies")
                    .content(objectMapper.writeValueAsString(movie))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
        }

        assertTrue(thrown.rootCause is IllegalArgumentException)
    }

}