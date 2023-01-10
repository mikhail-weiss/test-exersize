package com.test.movies.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

@Entity
@Table(
    name = "movie",
    uniqueConstraints = [UniqueConstraint(name = "UniqueTitleAndReleaseDate", columnNames = ["title", "release_date"])]
)
class Movie(
    @Column(nullable = false)
    @Max(255)
    @NotBlank(message = "Title is mandatory")
    val title: String,

    @Column(nullable = false, name = "release_date")
    val releaseDate: LocalDate,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
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
    var id: Long? = null
}
