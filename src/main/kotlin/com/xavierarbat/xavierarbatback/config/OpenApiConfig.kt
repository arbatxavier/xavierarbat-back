package com.xavierarbat.xavierarbatback.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Xavier Arbat Portfolio API")
                .version("1.0.0")
                .description(
                    """
                    REST API for Xavier Arbat's multidisciplinary artist portfolio.
                    
                    Supports **i18n** (en, es, ca) via the `Accept-Language` header.
                    
                    **Authentication:** Protected endpoints (POST, PUT, DELETE) require an `X-API-Key` header.
                    Public GET endpoints are open and do not require authentication.
                    
                    **Rate Limiting:** 60 requests per minute per IP address.
                    """.trimIndent()
                )
                .contact(
                    Contact()
                        .name("Xavier Arbat")
                        .url("https://xavierarbat.com")
                )
        )
        .servers(
            listOf(
                Server().url("https://api.xavierarbat.com").description("Production"),
                Server().url("http://localhost:8080").description("Local development")
            )
        )
        .components(
            Components()
                .addSecuritySchemes(
                    "ApiKeyAuth",
                    SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .`in`(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                        .description("API key for protected endpoints (POST, PUT, DELETE)")
                )
        )
        .addSecurityItem(SecurityRequirement().addList("ApiKeyAuth"))
}
