package com.xavierarbat.xavierarbatback.service

import com.xavierarbat.xavierarbatback.domain.Blog
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.repository.BlogRepository
import org.springframework.stereotype.Service

@Service
class BlogService(private val blogRepository: BlogRepository) {

    fun findAll(lang: String): List<BlogListDto> =
        blogRepository.findAll().map { it.toListDto(lang) }

    fun findBySlug(slug: String, lang: String): BlogDetailDto? =
        blogRepository.findById(slug).orElse(null)?.toDetailDto(lang)

    fun create(request: BlogCreateRequest): Blog {
        if (blogRepository.existsById(request.slug)) {
            throw IllegalArgumentException("Blog with slug '${request.slug}' already exists")
        }
        val blog = Blog(
            slug = request.slug,
            date = request.date,
            title = request.title,
            description = request.description,
            content = request.content
        )
        return blogRepository.save(blog)
    }

    fun update(slug: String, request: BlogUpdateRequest): Blog {
        val existing = blogRepository.findById(slug).orElseThrow {
            ResourceNotFoundException("Blog not found: $slug")
        }
        val updated = existing.copy(
            date = request.date ?: existing.date,
            title = request.title ?: existing.title,
            description = request.description ?: existing.description,
            content = request.content ?: existing.content
        )
        return blogRepository.save(updated)
    }

    fun delete(slug: String) {
        if (!blogRepository.existsById(slug)) {
            throw ResourceNotFoundException("Blog not found: $slug")
        }
        blogRepository.deleteById(slug)
    }
}
