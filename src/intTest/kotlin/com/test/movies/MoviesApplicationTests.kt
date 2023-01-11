package com.test.movies

import com.test.movies.dto.MovieDto
import com.test.movies.repository.MovieRepository
import com.test.movies.repository.StarRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [MoviesApplication::class]
)
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
@AutoConfigureWebTestClient
class MoviesApplicationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var movieRepository: MovieRepository

    @Autowired
    lateinit var starRepository: StarRepository

    @AfterEach
    fun tearDown() {
        this.movieRepository.deleteAll()
        this.starRepository.deleteAll()
    }

    protected fun create(movieDto: MovieDto) =
        webTestClient.post()
            .uri("/v1/movies")
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .bodyValue(movieDto)
            .exchange()

    protected fun getAll() = webTestClient.get()
        .uri("/v1/movies")
        .header(ACCEPT, APPLICATION_JSON_VALUE)
        .exchange()

   protected fun getOne(uri: String) = webTestClient.get()
        .uri(uri)
        .header(ACCEPT, APPLICATION_JSON_VALUE)
        .exchange()
    protected fun update(
        newMovieDto: MovieDto,
        uri: String
    ) = webTestClient.put().uri(uri)
        .header(ACCEPT, APPLICATION_JSON_VALUE)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(newMovieDto)
        .exchange()

    protected fun delete(uri: String) =
        webTestClient.delete().uri(uri).exchange()

}
