package com.test.movies

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.format.DateTimeFormatter


@SpringBootApplication
class MoviesApplication

fun main(args: Array<String>) {
    runApplication<MoviesApplication>(*args)
}

@Configuration
class WebConfiguration : WebMvcConfigurer {
    companion object {
        private const val dateFormat = "yyyy-MM-dd"
    }

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.simpleDateFormat(dateFormat)
            builder.serializers(LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)))
            builder.deserializers(LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)))

        }
    }
}


