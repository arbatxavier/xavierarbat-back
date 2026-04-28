package com.xavierarbat.xavierarbatback.domain

import com.xavierarbat.xavierarbatback.config.JsonbMapConverter
import jakarta.persistence.*

@Entity
@Table(name = "tag")
data class Tag(
    @Id
    @Column(length = 50)
    val key: String,

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val label: Map<String, String> = emptyMap()
)
