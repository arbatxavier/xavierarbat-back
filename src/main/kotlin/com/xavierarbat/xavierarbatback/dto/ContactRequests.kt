package com.xavierarbat.xavierarbatback.dto

data class ContactCreateRequest(
    val name: String,
    val display: Map<String, String>,
    val value: String,
    val link: String? = null,
    val showInFooter: Boolean = false
)

data class ContactUpdateRequest(
    val display: Map<String, String>? = null,
    val value: String? = null,
    val link: String? = null,
    val showInFooter: Boolean? = null
)
