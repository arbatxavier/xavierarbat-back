package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.commons.LocaleUtils.DEFAULT_LANGUAGE_CODE
import com.xavierarbat.xavierarbatback.commons.LocaleUtils.parseLang
import com.xavierarbat.xavierarbatback.dto.TagCreateRequest
import com.xavierarbat.xavierarbatback.dto.TagDto
import com.xavierarbat.xavierarbatback.dto.TagUpdateRequest
import com.xavierarbat.xavierarbatback.dto.toDto
import com.xavierarbat.xavierarbatback.service.TagService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tags")
@Tag(name = "Tags", description = "CRUD operations for project tags (i18n labels)")
class TagController(private val tagService: TagService) {

    @GetMapping("", "/")
    @Operation(summary = "List all tags", security = [])
    fun list(
        @Parameter(description = "Language code (en, es, ca)", example = "en")
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): List<TagDto> = tagService.findAll(parseLang(acceptLanguage))

    @GetMapping("/{key}")
    @Operation(
        summary = "Get a tag by key", security = [],
        responses = [
            ApiResponse(responseCode = "200", description = "Tag found"),
            ApiResponse(responseCode = "404", description = "Tag not found")
        ]
    )
    fun detail(
        @Parameter(description = "Tag key", example = "ILLUSTRATION")
        @PathVariable key: String,
        @Parameter(description = "Language code (en, es, ca)", example = "en")
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): TagDto = tagService.findByKey(key, parseLang(acceptLanguage))

    @PostMapping("", "/")
    @Operation(
        summary = "Create a tag",
        description = "Creates a new tag. The key is normalized to uppercase with underscores. Label is an i18n map {en, es, ca}. If omitted, a default label is generated.",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Tag created"),
            ApiResponse(responseCode = "400", description = "Tag already exists or invalid key"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun create(
        @RequestBody request: TagCreateRequest,
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): ResponseEntity<TagDto> {
        val tag = tagService.create(request)
        val dto = tag.toDto(parseLang(acceptLanguage))
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{key}")
    @Operation(
        summary = "Update a tag label",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "200", description = "Tag updated"),
            ApiResponse(responseCode = "404", description = "Tag not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun update(
        @Parameter(description = "Tag key", example = "ILLUSTRATION")
        @PathVariable key: String,
        @RequestBody request: TagUpdateRequest,
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): TagDto {
        val tag = tagService.update(key, request)
        return tag.toDto(parseLang(acceptLanguage))
    }

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a tag",
        description = "Deletes a tag and unlinks it from all projects.",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Tag deleted"),
            ApiResponse(responseCode = "404", description = "Tag not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun delete(
        @Parameter(description = "Tag key", example = "ILLUSTRATION")
        @PathVariable key: String
    ) = tagService.delete(key)
}
