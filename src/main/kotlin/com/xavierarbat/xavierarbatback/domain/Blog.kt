package com.xavierarbat.xavierarbatback.domain

import com.xavierarbat.xavierarbatback.config.JsonbMapConverter
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "blog")
data class Blog(
    @Id
    @Column(length = 255)
    val slug: String,

    @Column(nullable = false)
    val date: LocalDate,

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val title: Map<String, String> = emptyMap(),

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val description: Map<String, String> = emptyMap(),

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val content: Map<String, String> = emptyMap()
)
