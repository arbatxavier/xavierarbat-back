package com.xavierarbat.xavierarbatback.dto

data class TagDto(
    val key: String,
    val label: String
)

data class TagCreateRequest(
    val key: String,
    val label: Map<String, String>? = null
)

data class TagUpdateRequest(
    val label: Map<String, String>
)
