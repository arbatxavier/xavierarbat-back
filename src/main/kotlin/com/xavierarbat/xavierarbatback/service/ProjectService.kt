package com.xavierarbat.xavierarbatback.service

import com.xavierarbat.xavierarbatback.domain.Project
import com.xavierarbat.xavierarbatback.domain.enums.AspectRatio
import com.xavierarbat.xavierarbatback.domain.enums.ImageDisplay
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.repository.ProjectRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val tagService: TagService,
    private val imageService: ImageService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun findAll(lang: String): List<ProjectListDto> =
        projectRepository.findAll().map { it.toListDto(lang) }

    fun findBySlug(slug: String, lang: String): ProjectDetailDto? =
        projectRepository.findById(slug).orElse(null)?.toDetailDto(lang)

    @Transactional
    fun create(request: ProjectCreateRequest): Project {
        if (projectRepository.existsById(request.slug)) {
            throw IllegalArgumentException("Project with slug '${request.slug}' already exists")
        }
        val tags = tagService.resolveTagsByKeys(request.tags)
        val project = Project(
            slug = request.slug,
            date = request.date,
            image = request.image,
            title = request.title,
            description = request.description,
            content = request.content,
            tags = tags,
            imageDisplay = ImageDisplay.valueOf(request.imageDisplay),
            aspectRatio = AspectRatio.valueOf(request.aspectRatio),
            altImages = request.altImages
        )
        return projectRepository.save(project)
    }

    @Transactional
    fun update(slug: String, request: ProjectUpdateRequest): Project {
        val existing = projectRepository.findById(slug).orElseThrow {
            ResourceNotFoundException("Project not found: $slug")
        }
        val updated = existing.copy(
            date = request.date ?: existing.date,
            image = request.image ?: existing.image,
            title = request.title ?: existing.title,
            description = request.description ?: existing.description,
            content = request.content ?: existing.content,
            tags = request.tags?.let { tagService.resolveTagsByKeys(it) } ?: existing.tags,
            imageDisplay = request.imageDisplay?.let { ImageDisplay.valueOf(it) } ?: existing.imageDisplay,
            aspectRatio = request.aspectRatio?.let { AspectRatio.valueOf(it) } ?: existing.aspectRatio,
            altImages = request.altImages ?: existing.altImages
        )
        return projectRepository.save(updated)
    }

    @Transactional
    fun delete(slug: String) {
        val project = projectRepository.findById(slug).orElseThrow {
            ResourceNotFoundException("Project not found: $slug")
        }

        // Delete associated images from disk
        deleteProjectImages(project)

        projectRepository.delete(project)
    }

    /**
     * Deletes the main image and all alt images associated with a project
     * if they are stored in /uploads/projects/.
     */
    private fun deleteProjectImages(project: Project) {
        val allImagePaths = mutableListOf(project.image) + project.altImages

        for (path in allImagePaths) {
            if (path.startsWith("/uploads/projects/")) {
                val filename = path.substringAfterLast("/")
                try {
                    imageService.delete("projects", filename)
                } catch (e: Exception) {
                    log.warn("Could not delete image '$path' for project '${project.slug}': ${e.message}")
                }
            }
        }
    }
}
