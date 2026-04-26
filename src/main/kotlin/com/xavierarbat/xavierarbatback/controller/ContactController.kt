package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.commons.LocaleUtils.parseLang
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.service.ContactService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/contacts")
class ContactController(private val contactService: ContactService) {

    @GetMapping("", "/")
    fun list(@RequestHeader("Accept-Language", defaultValue = "en") acceptLanguage: String): List<ContactDto> {
        val lang = parseLang(acceptLanguage)
        return contactService.findAll(lang)
    }

    @PostMapping("", "/")
    fun create(@RequestBody request: ContactCreateRequest): ResponseEntity<ContactDto> {
        val contact = contactService.create(request)
        val dto = contact.toDto("en")
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{name}")
    fun update(
        @PathVariable name: String,
        @RequestBody request: ContactUpdateRequest
    ): ContactDto {
        val contact = contactService.update(name, request)
        return contact.toDto("en")
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable name: String) {
        contactService.delete(name)
    }
}
