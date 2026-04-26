package com.xavierarbat.xavierarbatback.dto

import java.time.LocalDate

data class BlogCreateRequest(
    val slug: String,
    val date: LocalDate,
    val title: Map<String, String>,
    val description: Map<String, String>,
    val content: Map<String, String>
)

data class BlogUpdateRequest(
    val date: LocalDate?,
    val title: Map<String, String>?,
    val description: Map<String, String>?,
    val content: Map<String, String>?
)
