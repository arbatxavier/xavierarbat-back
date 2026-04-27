package com.xavierarbat.xavierarbatback.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig(
    @Value("\${app.uploads.path:/app/uploads}")
    private val uploadsPath: String
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://xavierarbat.com", "http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Accept-Language", "Content-Type", "X-API-Key")
            .allowCredentials(false)
            .maxAge(3600)

        registry.addMapping("/uploads/**")
            .allowedOrigins("https://xavierarbat.com", "http://localhost:3000")
            .allowedMethods("GET", "OPTIONS")
            .allowCredentials(false)
            .maxAge(3600)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Serve uploaded images as static resources at /uploads/**
        val location = if (uploadsPath.endsWith("/")) "file:$uploadsPath" else "file:$uploadsPath/"
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location)
            .setCachePeriod(86400) // 24h browser cache
    }
}
