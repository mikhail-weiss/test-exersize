package com.test.movies.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "movie",
    uniqueConstraints = [UniqueConstraint(name = "UniqueNumberAndStatus", columnNames = ["title", "release_date"])]
)
class Movie(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, name = "release_date")
    val releaseDate: LocalDate,

    @ManyToMany(cascade = [CascadeType.ALL, CascadeType.MERGE])
    @JoinTable(
        name = "movie_star",
        joinColumns = [JoinColumn(name = "movie_id")],
        inverseJoinColumns = [JoinColumn(name = "star_id")]
    )
    var stars: Set<Star>
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_seq")
    @SequenceGenerator(name = "movie_seq", sequenceName = "movie_seq", allocationSize = 1)
    val id: Long? = null
}
