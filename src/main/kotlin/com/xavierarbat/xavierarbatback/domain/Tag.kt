package com.xavierarbat.xavierarbatback.domain

import jakarta.persistence.*

@Entity
@Table(name = "tag")
data class Tag(
    @Id
    @Column(length = 50)
    val key: String,

    @Column(length = 100, nullable = false)
    val label: String = key
)
