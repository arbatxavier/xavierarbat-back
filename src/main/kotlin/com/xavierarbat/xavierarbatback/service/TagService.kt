package com.xavierarbat.xavierarbatback.service

import com.xavierarbat.xavierarbatback.domain.Tag
import com.xavierarbat.xavierarbatback.dto.TagCreateRequest
import com.xavierarbat.xavierarbatback.dto.TagDto
import com.xavierarbat.xavierarbatback.dto.TagUpdateRequest
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.repository.ProjectRepository
import com.xavierarbat.xavierarbatback.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val DEFAULT_LANG = "en"

private fun Map<String, String>.localized(lang: String): String =
    this[lang] ?: this[DEFAULT_LANG] ?: values.firstOrNull() ?: ""

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val projectRepository: ProjectRepository
) {

    fun findAll(lang: String): List<TagDto> =
        tagRepository.findAll()
            .sortedBy { it.key }
            .map { TagDto(key = it.key, label = it.label.localized(lang)) }

    fun findByKey(key: String, lang: String): TagDto {
        val tag = tagRepository.findById(key).orElseThrow {
            ResourceNotFoundException("Tag not found: $key")
        }
        return TagDto(key = tag.key, label = tag.label.localized(lang))
    }

    fun create(request: TagCreateRequest): Tag {
        val normalizedKey = request.key.uppercase().replace(Regex("[^A-Z0-9]"), "_")
        if (tagRepository.existsById(normalizedKey)) {
            throw IllegalArgumentException("Tag '$normalizedKey' already exists")
        }
        val defaultLabel = normalizedKey.replace("_", " ").lowercase()
            .replaceFirstChar { it.uppercase() }
        val tag = Tag(
            key = normalizedKey,
            label = request.label ?: mapOf("en" to defaultLabel, "es" to defaultLabel, "ca" to defaultLabel)
        )
        return tagRepository.save(tag)
    }

    fun update(key: String, request: TagUpdateRequest): Tag {
        val existing = tagRepository.findById(key).orElseThrow {
            ResourceNotFoundException("Tag not found: $key")
        }
        val updated = existing.copy(label = request.label)
        return tagRepository.save(updated)
    }

    /**
     * Deletes a tag and removes it from all projects that reference it.
     */
    @Transactional
    fun delete(key: String) {
        val tag = tagRepository.findById(key).orElseThrow {
            ResourceNotFoundException("Tag not found: $key")
        }

        // Unlink from all projects
        val projects = projectRepository.findAll().filter { project ->
            project.tags.any { it.key == key }
        }
        for (project in projects) {
            val updated = project.copy(tags = project.tags.filter { it.key != key }.toSet())
            projectRepository.save(updated)
        }

        tagRepository.delete(tag)
    }

    /**
     * Resolves a set of tag keys to Tag entities.
     * Throws if any key does not exist.
     */
    fun resolveTagsByKeys(keys: Set<String>): Set<Tag> {
        if (keys.isEmpty()) return emptySet()
        val tags = tagRepository.findAllById(keys).toSet()
        val foundKeys = tags.map { it.key }.toSet()
        val missing = keys - foundKeys
        if (missing.isNotEmpty()) {
            throw IllegalArgumentException("Unknown tags: $missing")
        }
        return tags
    }
}
