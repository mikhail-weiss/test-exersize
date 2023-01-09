package com.test.movies.model

import jakarta.persistence.*

@Entity
@Table(name = "star")
class Star(
    @Column(nullable = false, unique = true)
    val name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "star_seq")
    @SequenceGenerator(name = "star_seq", sequenceName = "star_seq", allocationSize = 1)
    val id: Long? = null

    @ManyToMany(mappedBy = "stars", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var movies: Set<Movie> = setOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Star

        if (name != other.name) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Star(name='$name', id=$id)"
    }
}