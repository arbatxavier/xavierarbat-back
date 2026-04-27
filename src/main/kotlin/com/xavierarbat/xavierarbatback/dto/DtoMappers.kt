package com.xavierarbat.xavierarbatback.dto

import com.xavierarbat.xavierarbatback.domain.Blog
import com.xavierarbat.xavierarbatback.domain.Contact
import com.xavierarbat.xavierarbatback.domain.Project

private const val DEFAULT_LANG = "en"

/**
 * Extrae el valor traducido de un mapa JSONB {lang -> text}.
 * Si el idioma solicitado no existe, cae a "es" y luego al primer valor disponible.
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
