package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.commons.LocaleUtils.DEFAULT_LANGUAGE_CODE
import com.xavierarbat.xavierarbatback.commons.LocaleUtils.parseLang
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.service.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/projects")
class ProjectController(private val projectService: ProjectService) {

    @GetMapping("", "/")
    fun list(@RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String): List<ProjectListDto> {
        val lang = parseLang(acceptLanguage)
        return projectService.findAll(lang)
    }

    @GetMapping("/{slug}")
    fun detail(
        @PathVariable slug: String,
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): ProjectDetailDto {
        val lang = parseLang(acceptLanguage)
        return projectService.findBySlug(slug, lang)
            ?: throw ResourceNotFoundException("Project not found: $slug")
    }

    @PostMapping("", "/")
    fun create(@RequestBody request: ProjectCreateRequest): ResponseEntity<ProjectDetailDto> {
        val project = projectService.create(request)
        val dto = project.toDetailDto("en")
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{slug}")
    fun update(
        @PathVariable slug: String,
        @RequestBody request: ProjectUpdateRequest
    ): ProjectDetailDto {
        val project = projectService.update(slug, request)
        return project.toDetailDto("en")
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable slug: String) {
        projectService.delete(slug)
    }
}
