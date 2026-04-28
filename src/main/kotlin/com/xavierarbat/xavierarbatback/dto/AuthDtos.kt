package com.xavierarbat.xavierarbatback.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class TokenResponse(
    val token: String,
    val expiresIn: Long
)
