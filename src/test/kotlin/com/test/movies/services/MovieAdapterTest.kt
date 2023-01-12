package com.test.movies.services

import com.test.movies.dto.MovieDto
import com.test.movies.model.Movie
import com.test.movies.model.Star
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MovieAdapterTest {
    @Test
    fun `transform to dto`() {

        val releaseDate = DEFAULT_LOCAL_DATE
        val title = "title"
        val stars = setOf(Star("star1"), Star("star2"))
        val movie = Movie(title, releaseDate, stars).apply { id = 1L }
        val actualDto = toDto(movie)

        assertEquals(actualDto.id, 1L)
        assertEquals(actualDto.title, title)
        assertEquals(actualDto.releaseDate, releaseDate)
        assertEquals(actualDto.stars, stars.map { it.name }.toSet())
    }

    @Test
    fun `transform from model without id should work`() {
        val releaseDate = DEFAULT_LOCAL_DATE
        val title = "title"
        val stars = setOf(Star("star1"), Star("star2"))
        val movie = Movie(title, releaseDate, stars)
        val actualDto = toDto(movie)

        assertNull(actualDto.id)
        assertEquals(actualDto.title, title)
        assertEquals(actualDto.releaseDate, releaseDate)
        assertEquals(actualDto.stars, stars.map { it.name }.toSet())
    }

    @Test
    fun `transform to model should work`() {
        val releaseDate = DEFAULT_LOCAL_DATE
        val title = "title"
        val stars = setOf("star1", "star2")
        val movie = MovieDto(title, releaseDate, stars)
        val actualDto = fromDto(movie)

        assertNull(actualDto.id)
        assertEquals(actualDto.title, title)
        assertEquals(actualDto.releaseDate, releaseDate)
        assertEquals(actualDto.stars, stars.map { Star(it) }.toSet())
    }

    @Test
    fun `transform to model should pass id correctly`() {
        val releaseDate = DEFAULT_LOCAL_DATE
        val title = "title"
        val stars = setOf("star1", "star2")
        val movie = MovieDto(title, releaseDate, stars, 1L)
        val actualDto = fromDto(movie)

        assertEquals(actualDto.id, 1L)
        assertEquals(actualDto.title, title)
        assertEquals(actualDto.releaseDate, releaseDate)
        assertEquals(actualDto.stars, stars.map { Star(it) }.toSet())
    }
}