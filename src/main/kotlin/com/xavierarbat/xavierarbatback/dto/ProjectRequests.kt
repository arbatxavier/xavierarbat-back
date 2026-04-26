package com.xavierarbat.xavierarbatback.dto

import java.time.LocalDate

data class ProjectCreateRequest(
    val slug: String,
    val date: LocalDate,
    val image: String,
    val title: Map<String, String>,
    val description: Map<String, String>,
    val content: Map<String, String>,
    val tags: Set<String> = emptySet(),
    val imageDisplay: String = "COVER",
    val aspectRatio: String = "FOURTHIRDS",
    val altImages: List<String> = emptyList()
)

data class ProjectUpdateRequest(
    val date: LocalDate? = null,
    val image: String? = null,
    val title: Map<String, String>? = null,
    val description: Map<String, String>? = null,
    val content: Map<String, String>? = null,
    val tags: Set<String>? = null,
    val imageDisplay: String? = null,
    val aspectRatio: String? = null,
    val altImages: List<String>? = null
)
