package com.xavierarbat.xavierarbatback.dto

import com.xavierarbat.xavierarbatback.domain.Blog
import com.xavierarbat.xavierarbatback.domain.Contact
import com.xavierarbat.xavierarbatback.domain.Project
import com.xavierarbat.xavierarbatback.domain.Tag

private const val DEFAULT_LANG = "en"

/**
 * Resolves a localized value from a JSONB map {lang -> text}.
 * Fallback: requested lang -> "en" -> first available.
 */
private fun Map<String, String>.localized(lang: String): String =
    this[lang] ?: this[DEFAULT_LANG] ?: values.firstOrNull() ?: ""

// =============================================
// Blog mappings
// =============================================

fun Blog.toListDto(lang: String): BlogListDto = BlogListDto(
    slug = slug,
    date = date,
    title = title.localized(lang),
    shortDescription = description.localized(lang)
)

fun Blog.toDetailDto(lang: String): BlogDetailDto = BlogDetailDto(
    slug = slug,
    date = date,
    title = title.localized(lang),
    description = description.localized(lang),
    content = content.localized(lang)
)

// =============================================
// Contact mappings
// =============================================

fun Contact.toDto(lang: String): ContactDto = ContactDto(
    name = name,
    display = display.localized(lang),
    value = value,
    link = link,
    showInFooter = showInFooter
)

// =============================================
// Tag mappings
// =============================================

fun Tag.toDto(lang: String): TagDto = TagDto(
    key = key,
    label = label.localized(lang)
)

// =============================================
// Project mappings
// =============================================

fun Project.toListDto(lang: String): ProjectListDto = ProjectListDto(
    slug = slug,
    date = date,
    image = image,
    title = title.localized(lang),
    shortDescription = description.localized(lang),
    tags = tags.map { it.key }.toSet(),
    imageDisplay = imageDisplay.name,
    aspectRatio = aspectRatio.name,
    altImages = altImages
)

fun Project.toDetailDto(lang: String): ProjectDetailDto = ProjectDetailDto(
    slug = slug,
    date = date,
    image = image,
    title = title.localized(lang),
    description = description.localized(lang),
    content = content.localized(lang),
    tags = tags.map { it.key }.toSet(),
    imageDisplay = imageDisplay.name,
    aspectRatio = aspectRatio.name,
    altImages = altImages
)
