package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.commons.LocaleUtils.DEFAULT_LANGUAGE_CODE
import com.xavierarbat.xavierarbatback.commons.LocaleUtils.parseLang
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.service.BlogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/blogs")
@Tag(name = "Blogs", description = "CRUD operations for blog posts")
class BlogController(private val blogService: BlogService) {

    @GetMapping("", "/")
    @Operation(summary = "List all blog posts", security = [])
    fun list(
        @Parameter(description = "Language code (en, es, ca)", example = "en")
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): List<BlogListDto> {
        val lang = parseLang(acceptLanguage)
        return blogService.findAll(lang)
    }

    @GetMapping("/{slug}")
    @Operation(
        summary = "Get blog post detail", security = [],
        responses = [
            ApiResponse(responseCode = "200", description = "Blog found"),
            ApiResponse(responseCode = "404", description = "Blog not found")
        ]
    )
    fun detail(
        @Parameter(description = "Blog slug", example = "my-first-post")
        @PathVariable slug: String,
        @Parameter(description = "Language code (en, es, ca)", example = "en")
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): BlogDetailDto {
        val lang = parseLang(acceptLanguage)
        return blogService.findBySlug(slug, lang)
            ?: throw ResourceNotFoundException("Blog not found: $slug")
    }

    @PostMapping("", "/")
    @Operation(
        summary = "Create a blog post",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Blog created"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun create(@RequestBody request: BlogCreateRequest): ResponseEntity<BlogDetailDto> {
        val blog = blogService.create(request)
        val dto = blog.toDetailDto("en")
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{slug}")
    @Operation(
        summary = "Update a blog post",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "200", description = "Blog updated"),
            ApiResponse(responseCode = "404", description = "Blog not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun update(
        @Parameter(description = "Blog slug") @PathVariable slug: String,
        @RequestBody request: BlogUpdateRequest
    ): BlogDetailDto {
        val blog = blogService.update(slug, request)
        return blog.toDetailDto("en")
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a blog post",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Blog deleted"),
            ApiResponse(responseCode = "404", description = "Blog not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun delete(@Parameter(description = "Blog slug") @PathVariable slug: String) {
        blogService.delete(slug)
    }
}
