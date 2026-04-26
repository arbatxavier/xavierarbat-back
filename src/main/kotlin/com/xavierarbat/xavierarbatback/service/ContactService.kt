package com.xavierarbat.xavierarbatback.service

import com.xavierarbat.xavierarbatback.domain.Contact
import com.xavierarbat.xavierarbatback.dto.*
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import com.xavierarbat.xavierarbatback.repository.ContactRepository
import org.springframework.stereotype.Service

@Service
class ContactService(private val contactRepository: ContactRepository) {

    fun findAll(lang: String): List<ContactDto> =
        contactRepository.findAll().map { it.toDto(lang) }

    fun create(request: ContactCreateRequest): Contact {
        if (contactRepository.existsById(request.name)) {
            throw IllegalArgumentException("Contact with name '${request.name}' already exists")
        }
        val contact = Contact(
            name = request.name,
            display = request.display,
            value = request.value,
            link = request.link,
            showInFooter = request.showInFooter
        )
        return contactRepository.save(contact)
    }

    fun update(name: String, request: ContactUpdateRequest): Contact {
        val existing = contactRepository.findById(name).orElseThrow {
            ResourceNotFoundException("Contact not found: $name")
        }
        val updated = existing.copy(
            display = request.display ?: existing.display,
            value = request.value ?: existing.value,
            link = request.link ?: existing.link,
            showInFooter = request.showInFooter ?: existing.showInFooter
        )
        return contactRepository.save(updated)
    }

    fun delete(name: String) {
        if (!contactRepository.existsById(name)) {
            throw ResourceNotFoundException("Contact not found: $name")
        }
        contactRepository.deleteById(name)
    }
}
