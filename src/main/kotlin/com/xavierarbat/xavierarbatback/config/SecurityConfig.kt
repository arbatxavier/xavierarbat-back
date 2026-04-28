package com.xavierarbat.xavierarbatback.config

import com.xavierarbat.xavierarbatback.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import tools.jackson.module.kotlin.jacksonObjectMapper

@Configuration
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                authorize("/swagger-ui/**", permitAll)
                authorize("/swagger-ui.html", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/uploads/**", permitAll)
                authorize(HttpMethod.POST, "/api/v1/auth/**", permitAll)
                authorize(HttpMethod.GET, "/api/**", permitAll)
                authorize(HttpMethod.OPTIONS, "/api/**", permitAll)
                authorize(HttpMethod.POST, "/api/**", hasRole("ADMIN"))
                authorize(HttpMethod.PUT, "/api/**", hasRole("ADMIN"))
                authorize(HttpMethod.DELETE, "/api/**", hasRole("ADMIN"))
                authorize(anyRequest, permitAll)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthFilter)
            exceptionHandling {
                authenticationEntryPoint = HttpServletAuthenticationEntryPoint()
                accessDeniedHandler = HttpServletAccessDeniedHandler()
            }
        }
        return http.build()
    }

    class HttpServletAuthenticationEntryPoint :
        org.springframework.security.web.AuthenticationEntryPoint {

        override fun commence(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authException: org.springframework.security.core.AuthenticationException
        ) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication required. Provide a valid Bearer token.", request.requestURI)
        }
    }

    class HttpServletAccessDeniedHandler :
        org.springframework.security.web.access.AccessDeniedHandler {

        override fun handle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            accessDeniedException: org.springframework.security.access.AccessDeniedException
        ) {
            writeErrorResponse(response, HttpStatus.FORBIDDEN, "Access denied.", request.requestURI)
        }
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()

        fun writeErrorResponse(response: HttpServletResponse, status: HttpStatus, message: String, path: String) {
            response.status = status.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            val error = ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                path = path
            )
            response.writer.write(objectMapper.writeValueAsString(error))
        }
    }
}
