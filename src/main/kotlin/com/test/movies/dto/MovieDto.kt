package com.test.movies.dto

import java.time.LocalDate

data class MovieDto(
    val title: String,
    val releaseDate: LocalDate,
    val id: Long? = null,
    val stars: Set<String> = setOf(),
)
