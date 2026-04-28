package com.xavierarbat.xavierarbatback.config

import com.xavierarbat.xavierarbatback.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(AUTH_HEADER)

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            val token = header.substring(BEARER_PREFIX.length)
            val username = jwtService.getUsername(token)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val auth = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}
