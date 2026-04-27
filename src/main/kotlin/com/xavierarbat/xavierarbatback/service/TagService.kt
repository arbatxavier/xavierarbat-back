package com.xavierarbat.xavierarbatback.service

import com.xavierarbat.xavierarbatback.domain.Tag
import com.xavierarbat.xavierarbatback.dto.TagCreateRequest
import com.xavierarbat.xavierarbatback.dto.TagDto
import com.xavierarbat.xavierarbatback.dto.TagUpdateRequest
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.repository.TagRepository
import org.springframework.stereotype.Service

@Service
class TagService(private val tagRepository: TagRepository) {

    fun findAll(): List<TagDto> =
        tagRepository.findAll()
            .sortedBy { it.key }
            .map { TagDto(key = it.key, label = it.label) }

    fun findByKey(key: String): TagDto {
        val tag = tagRepository.findById(key).orElseThrow {
            ResourceNotFoundException("Tag not found: $key")
        }
        return TagDto(key = tag.key, label = tag.label)
    }

    fun create(request: TagCreateRequest): TagDto {
        val normalizedKey = request.key.uppercase().replace(Regex("[^A-Z0-9]"), "_")
        if (tagRepository.existsById(normalizedKey)) {
            throw IllegalArgumentException("Tag '$normalizedKey' already exists")
        }
        val tag = Tag(
            key = normalizedKey,
            label = request.label ?: normalizedKey.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() }
        )
        val saved = tagRepository.save(tag)
        return TagDto(key = saved.key, label = saved.label)
    }

    fun update(key: String, request: TagUpdateRequest): TagDto {
        val existing = tagRepository.findById(key).orElseThrow {
            ResourceNotFoundException("Tag not found: $key")
        }
        val updated = existing.copy(label = request.label)
        val saved = tagRepository.save(updated)
        return TagDto(key = saved.key, label = saved.label)
    }

    fun delete(key: String) {
        if (!tagRepository.existsById(key)) {
            throw ResourceNotFoundException("Tag not found: $key")
        }
        tagRepository.deleteById(key)
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
