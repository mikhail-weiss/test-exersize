package com.test.movies.controllers

import com.test.movies.dto.MovieDto
import com.test.movies.services.MovieService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/movies")
@ApiResponses(value = [ApiResponse(responseCode = "500", description = "Server error", content = [Content()])])
class MovieController(val movieService: MovieService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Fetch all movies")

    fun findAll(): Set<MovieDto> {
        return movieService.findAll()
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "404",
            description = "There is no movie with such id",
            content = [Content()]
        )]
    )
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Fetch a movies by its id")
    fun findOne(@PathVariable id: Long): MovieDto {
        return movieService.findById(id)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(
        value =
        [ApiResponse(
            responseCode = "400",
            description = "Provided movie object is misconfigured",
            content = [Content()]
        )]
    )
    @Operation(summary = "Create new movie")
    fun create(@RequestBody movie: MovieDto): MovieDto {
        return movieService.create(movie)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "404",
            description = "There is no movie with such id",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "204",
            description = "Successfully deleted",
            content = [Content()]
        )]
    )
    @Operation(summary = "Delete a movie by id")
    fun delete(@PathVariable id: Long) {
        movieService.delete(id)
    }

    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "400",
                description = "Provided movie object is misconfigured",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "There is no movie with such id", content = [Content()])
        ]
    )
    @Operation(summary = "Update a movie by id")
    fun update(@PathVariable id: Long, @RequestBody movie: MovieDto): MovieDto {
        return movieService.update(id, movie)
    }
}
