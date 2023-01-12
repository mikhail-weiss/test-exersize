package com.test.movies.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate

data class MovieDto(
    @NotEmpty
    val title: String,
    val releaseDate: LocalDate,
    @Min(1)
    @Max(100)
    val stars: Set<String> = setOf(),
    val id: Long? = null,
)
