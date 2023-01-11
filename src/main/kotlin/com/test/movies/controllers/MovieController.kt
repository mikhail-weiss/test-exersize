package com.test.movies.controllers

import com.test.movies.dto.MovieDto
import com.test.movies.services.MovieService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/movies")
class MovieController(val movieService: MovieService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(): List<MovieDto> {
        return movieService.findAll()
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findOne(@PathVariable id: Long): MovieDto {
        return movieService.findById(id)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun create(@RequestBody movie: MovieDto): MovieDto {
        return movieService.create(movie)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        movieService.delete(id)
    }

    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@PathVariable id: Long, @RequestBody movie: MovieDto): MovieDto {
        return movieService.update(id, movie)
    }

//    //TODO: maybe that's better validation
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun handleValidationExceptions(ex: MethodArgumentNotValidException): Map<String, String> {
//        val errors = mutableMapOf<String, String>()
//        ex.bindingResult.allErrors.forEach { error ->
//            val fieldName: String? = error?.objectName
//            val errorMessage = error.defaultMessage;
//            if (fieldName != null && errorMessage != null) {
//                errors[fieldName] = errorMessage;
//            }
//        }
//        return errors;
//    }

}
