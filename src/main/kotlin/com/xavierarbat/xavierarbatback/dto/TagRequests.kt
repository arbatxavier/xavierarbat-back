package com.xavierarbat.xavierarbatback.dto

data class TagDto(
    val key: String,
    val label: String
)

data class TagCreateRequest(
    val key: String,
    val label: String? = null
)

data class TagUpdateRequest(
    val label: String
)
