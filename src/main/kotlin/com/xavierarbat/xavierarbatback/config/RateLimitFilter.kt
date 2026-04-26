package com.xavierarbat.xavierarbatback.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Simple token-bucket rate limiter per IP address.
 * Allows [maxTokens] requests per IP, refilling at 1 token per second.
 * A background cleanup removes inactive IPs every [cleanupIntervalMs].
 */
class RateLimitFilter(
    private val maxTokens: Long = 60,
    private val cleanupIntervalMs: Long = 600_000 // 10 minutes
) : OncePerRequestFilter() {

    private data class Bucket(
        val tokens: AtomicLong = AtomicLong(60),
        val lastRefill: AtomicLong = AtomicLong(System.currentTimeMillis()),
        val lastAccess: AtomicLong = AtomicLong(System.currentTimeMillis())
    )

    private val buckets = ConcurrentHashMap<String, Bucket>()
    private val lastCleanup = AtomicLong(System.currentTimeMillis())

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        cleanupIfNeeded()

        val ip = resolveClientIp(request)
        val bucket = buckets.computeIfAbsent(ip) { Bucket(tokens = AtomicLong(maxTokens)) }
        refill(bucket)
        bucket.lastAccess.set(System.currentTimeMillis())

        if (bucket.tokens.decrementAndGet() < 0) {
            bucket.tokens.incrementAndGet() // restore so it doesn't go deeply negative
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.contentType = "application/json"
            response.writer.write(
                """{"status":429,"error":"Too Many Requests","message":"Rate limit exceeded. Try again later.","path":"${request.requestURI}"}"""
            )
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun refill(bucket: Bucket) {
        val now = System.currentTimeMillis()
        val last = bucket.lastRefill.get()
        val elapsedSeconds = (now - last) / 1000

        if (elapsedSeconds > 0 && bucket.lastRefill.compareAndSet(last, now)) {
            val newTokens = (bucket.tokens.get() + elapsedSeconds).coerceAtMost(maxTokens)
            bucket.tokens.set(newTokens)
        }
    }

    private fun resolveClientIp(request: HttpServletRequest): String =
        request.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim()
            ?: request.remoteAddr

    private fun cleanupIfNeeded() {
        val now = System.currentTimeMillis()
        val last = lastCleanup.get()
        if (now - last > cleanupIntervalMs && lastCleanup.compareAndSet(last, now)) {
            val threshold = now - cleanupIntervalMs
            buckets.entries.removeIf { it.value.lastAccess.get() < threshold }
        }
    }
}

@Configuration
class RateLimitConfig {

    @Bean
    fun rateLimitFilterRegistration(): FilterRegistrationBean<RateLimitFilter> {
        val registration = FilterRegistrationBean(RateLimitFilter(maxTokens = 60))
        registration.addUrlPatterns("/api/*")
        registration.order = Ordered.HIGHEST_PRECEDENCE
        return registration
    }
}
