package com.xavierarbat.xavierarbatback.dto

import java.time.LocalDate

// =============================================
// Blog DTOs
// =============================================

data class BlogListDto(
    val slug: String,
    val date: LocalDate,
    val title: String,
    val shortDescription: String
)

data class BlogDetailDto(
    val slug: String,
    val date: LocalDate,
    val title: String,
    val description: String,
    val content: String
)

// =============================================
// Contact DTOs
// =============================================

data class ContactDto(
    val name: String,
    val display: String,
    val value: String,
    val link: String?,
    val showInFooter: Boolean
)

// =============================================
// Project DTOs
// =============================================

data class ProjectListDto(
    val slug: String,
    val date: LocalDate,
    val image: String,
    val title: String,
    val shortDescription: String,
    val tags: Set<String>,
    val imageDisplay: String,
    val aspectRatio: String,
    val altImages: List<String>
)

data class ProjectDetailDto(
    val slug: String,
    val date: LocalDate,
    val image: String,
    val title: String,
    val description: String,
    val content: String,
    val tags: Set<String>,
    val imageDisplay: String,
    val aspectRatio: String,
    val altImages: List<String>
)
