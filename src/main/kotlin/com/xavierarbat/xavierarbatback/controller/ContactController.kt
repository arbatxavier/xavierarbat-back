package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.commons.LocaleUtils.parseLang
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.service.ContactService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/contacts")
@Tag(name = "Contacts", description = "CRUD operations for contact links (social media, email, etc.)")
class ContactController(private val contactService: ContactService) {

    @GetMapping("", "/")
    @Operation(summary = "List all contacts", security = [])
    fun list(
        @Parameter(description = "Language code (en, es, ca)", example = "en")
        @RequestHeader("Accept-Language", defaultValue = "en") acceptLanguage: String
    ): List<ContactDto> {
        val lang = parseLang(acceptLanguage)
        return contactService.findAll(lang)
    }

    @PostMapping("", "/")
    @Operation(
        summary = "Create a contact",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Contact created"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun create(@RequestBody request: ContactCreateRequest): ResponseEntity<ContactDto> {
        val contact = contactService.create(request)
        val dto = contact.toDto("en")
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @PutMapping("/{name}")
    @Operation(
        summary = "Update a contact",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "200", description = "Contact updated"),
            ApiResponse(responseCode = "404", description = "Contact not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun update(
        @Parameter(description = "Contact name (PK)", example = "instagram")
        @PathVariable name: String,
        @RequestBody request: ContactUpdateRequest
    ): ContactDto {
        val contact = contactService.update(name, request)
        return contact.toDto("en")
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a contact",
        security = [SecurityRequirement(name = "BearerAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Contact deleted"),
            ApiResponse(responseCode = "404", description = "Contact not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid token")
        ]
    )
    fun delete(@Parameter(description = "Contact name") @PathVariable name: String) {
        contactService.delete(name)
    }
}
