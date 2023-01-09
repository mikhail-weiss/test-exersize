package com.test.movies.repository

import com.test.movies.model.Star
import jakarta.annotation.Nullable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StarRepository : JpaRepository<Star, Long> {
    @Nullable
    fun findByName(name: String): Star?
}
