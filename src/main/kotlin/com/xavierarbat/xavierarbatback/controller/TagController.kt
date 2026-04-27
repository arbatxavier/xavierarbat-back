package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.dto.TagCreateRequest
import com.xavierarbat.xavierarbatback.dto.TagDto
import com.xavierarbat.xavierarbatback.dto.TagUpdateRequest
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
@Tag(name = "Tags", description = "CRUD operations for project tags")
class TagController(private val tagService: TagService) {

    @GetMapping("", "/")
    @Operation(summary = "List all tags", security = [])
    fun list(): List<TagDto> = tagService.findAll()

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
        @PathVariable key: String
    ): TagDto = tagService.findByKey(key)

    @PostMapping("", "/")
    @Operation(
        summary = "Create a tag",
        description = "Creates a new tag. The key is normalized to uppercase with underscores. If no label is provided, it is auto-generated from the key.",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Tag created"),
            ApiResponse(responseCode = "400", description = "Tag already exists or invalid key"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun create(@RequestBody request: TagCreateRequest): ResponseEntity<TagDto> {
        val dto = tagService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{key}")
    @Operation(
        summary = "Update a tag label",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "200", description = "Tag updated"),
            ApiResponse(responseCode = "404", description = "Tag not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun update(
        @Parameter(description = "Tag key", example = "ILLUSTRATION")
        @PathVariable key: String,
        @RequestBody request: TagUpdateRequest
    ): TagDto = tagService.update(key, request)

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a tag",
        description = "Deletes a tag. Projects using this tag will lose the association.",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Tag deleted"),
            ApiResponse(responseCode = "404", description = "Tag not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun delete(
        @Parameter(description = "Tag key", example = "ILLUSTRATION")
        @PathVariable key: String
    ) = tagService.delete(key)
}
