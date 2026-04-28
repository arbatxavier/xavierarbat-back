package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.dto.LoginRequest
import com.xavierarbat.xavierarbatback.dto.TokenResponse
import com.xavierarbat.xavierarbatback.service.JwtService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login to obtain a JWT Bearer token")
class AuthController(
    private val jwtService: JwtService,
    @Value("\${app.admin.username}") private val adminUsername: String,
    @Value("\${app.admin.password}") private val adminPassword: String,
    @Value("\${app.jwt.expiration-ms:86400000}") private val expirationMs: Long
) {

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticate with username and password to receive a JWT token. Use the token in the `Authorization: Bearer <token>` header for protected endpoints.",
        security = [],
        responses = [
            ApiResponse(responseCode = "200", description = "Login successful, returns JWT token"),
            ApiResponse(responseCode = "401", description = "Invalid credentials")
        ]
    )
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        if (request.username != adminUsername || request.password != adminPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val token = jwtService.generateToken(request.username)
        return ResponseEntity.ok(TokenResponse(token = token, expiresIn = expirationMs / 1000))
    }
}
