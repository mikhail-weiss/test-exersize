package com.test.movies.controllers.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class MovieIsWrongException(message: String) : RuntimeException(message)