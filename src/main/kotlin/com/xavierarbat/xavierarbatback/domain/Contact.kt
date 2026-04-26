package com.xavierarbat.xavierarbatback.domain

import com.xavierarbat.xavierarbatback.config.JsonbMapConverter
import jakarta.persistence.*

@Entity
@Table(name = "contact")
data class Contact(
    @Id
    @Column(length = 255)
    val name: String,

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val display: Map<String, String> = emptyMap(),

    @Column(nullable = false)
    val value: String = "",

    @Column(length = 512)
    val link: String? = null,

    @Column(name = "show_in_footer", nullable = false)
    val showInFooter: Boolean = false
)
