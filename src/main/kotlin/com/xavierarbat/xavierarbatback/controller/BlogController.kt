package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.commons.LocaleUtils.DEFAULT_LANGUAGE_CODE
import com.xavierarbat.xavierarbatback.commons.LocaleUtils.parseLang
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.service.BlogService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/blogs")
class BlogController(private val blogService: BlogService) {

    @GetMapping("", "/")
    fun list(@RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String): List<BlogListDto> {
        val lang = parseLang(acceptLanguage)
        return blogService.findAll(lang)
    }

    @GetMapping("/{slug}")
    fun detail(
        @PathVariable slug: String,
        @RequestHeader("Accept-Language", defaultValue = DEFAULT_LANGUAGE_CODE) acceptLanguage: String
    ): BlogDetailDto {
        val lang = parseLang(acceptLanguage)
        return blogService.findBySlug(slug, lang)
            ?: throw ResourceNotFoundException("Blog not found: $slug")
    }

    @PostMapping("", "/")
    fun create(@RequestBody request: BlogCreateRequest): ResponseEntity<BlogDetailDto> {
        val blog = blogService.create(request)
        val dto = blog.toDetailDto("en")
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{slug}")
    fun update(
        @PathVariable slug: String,
        @RequestBody request: BlogUpdateRequest
    ): BlogDetailDto {
        val blog = blogService.update(slug, request)
        return blog.toDetailDto("en")
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable slug: String) {
        blogService.delete(slug)
    }
}
