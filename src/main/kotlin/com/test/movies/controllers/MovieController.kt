package com.test.movies.controllers

import com.test.movies.dto.MovieDto
import com.test.movies.services.MovieService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/movies")
class MovieController(val movieService: MovieService) {


    @GetMapping
    fun findAll(): List<MovieDto> {
        return movieService.findAll()
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody movie: MovieDto): MovieDto {
        if (movie.stars.isEmpty()) {
            throw IllegalArgumentException("There must be at least one entry in the stars list")
        }
        return movieService.create(movie)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        movieService.delete(id)
    }
//
//    @PutMapping
//    fun index(@RequestParam("name") name: String): String {
//        println("asdasd")
//        return "Hello, $name!"
//    }
}
