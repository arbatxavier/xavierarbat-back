package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.commons.LocaleUtils.DEFAULT_LANGUAGE_CODE
import com.xavierarbat.xavierarbatback.commons.LocaleUtils.parseLang
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.service.ProjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "CRUD operations for portfolio projects")
class ProjectController(private val projectService: ProjectService) {

    @GetMapping("", "/")
    @Operation(
        summary = "List all projects",
        description = "Returns a list of all projects with localized title, description, and image. Language is resolved from Accept-Language header.",
        security = []
    )
    fun list(
        @Parameter(description = "Language code (en, es, ca)", example = "en")
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): List<ProjectListDto> {
        val lang = parseLang(acceptLanguage)
        return projectService.findAll(lang)
    }

    @GetMapping("/{slug}")
    @Operation(
        summary = "Get project detail",
        description = "Returns full project detail including content, tags, and alternative images.",
        security = [],
        responses = [
            ApiResponse(responseCode = "200", description = "Project found"),
            ApiResponse(responseCode = "404", description = "Project not found")
        ]
    )
    fun detail(
        @Parameter(description = "Project slug (URL-friendly identifier)", example = "berserk-tribute-ink")
        @PathVariable slug: String,
        @Parameter(description = "Language code (en, es, ca)", example = "en")
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): ProjectDetailDto {
        val lang = parseLang(acceptLanguage)
        return projectService.findBySlug(slug, lang)
            ?: throw ResourceNotFoundException("Project not found: $slug")
    }

    @PostMapping("", "/")
    @Operation(
        summary = "Create a project",
        description = "Creates a new project. Requires authentication.",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Project created"),
            ApiResponse(responseCode = "400", description = "Invalid request or slug already exists"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun create(@RequestBody request: ProjectCreateRequest): ResponseEntity<ProjectDetailDto> {
        val project = projectService.create(request)
        val dto = project.toDetailDto("en")
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{slug}")
    @Operation(
        summary = "Update a project",
        description = "Updates an existing project. Only provided fields are updated. Requires authentication.",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "200", description = "Project updated"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun update(
        @Parameter(description = "Project slug", example = "berserk-tribute-ink")
        @PathVariable slug: String,
        @RequestBody request: ProjectUpdateRequest
    ): ProjectDetailDto {
        val project = projectService.update(slug, request)
        return project.toDetailDto("en")
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a project",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Project deleted"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun delete(
        @Parameter(description = "Project slug", example = "berserk-tribute-ink")
        @PathVariable slug: String
    ) {
        projectService.delete(slug)
    }
}
