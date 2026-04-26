package com.xavierarbat.xavierarbatback.controller

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ContactCrudTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Value("\${app.api-key}")
    private lateinit var apiKey: String

    companion object {
        private const val TEST_NAME = "test-contact"
    }

    private val createBody = """
        {
            "name": "$TEST_NAME",
            "display": {"en": "Test Contact", "es": "Contacto de Prueba", "ca": "Contacte de Prova"},
            "value": "test@example.com",
            "link": "mailto:test@example.com",
            "showInFooter": true
        }
    """.trimIndent()

    @Test
    @Order(1)
    fun `POST contact without API key should return 401`() {
        mockMvc.perform(
            post("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    @Order(2)
    fun `POST contact with API key should return 201`() {
        // Clean up first
        mockMvc.perform(
            delete("/api/v1/contacts/$TEST_NAME")
                .header("X-API-Key", apiKey)
        )

        mockMvc.perform(
            post("/api/v1/contacts")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value(TEST_NAME))
            .andExpect(jsonPath("$.display").value("Test Contact"))
            .andExpect(jsonPath("$.value").value("test@example.com"))
            .andExpect(jsonPath("$.showInFooter").value(true))
    }

    @Test
    @Order(3)
    fun `PUT contact with API key should update it`() {
        val updateBody = """
            {
                "display": {"en": "Updated Contact", "es": "Contacto Actualizado", "ca": "Contacte Actualitzat"},
                "showInFooter": false
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/contacts/$TEST_NAME")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.display").value("Updated Contact"))
            .andExpect(jsonPath("$.showInFooter").value(false))
    }

    @Test
    @Order(4)
    fun `DELETE contact with API key should return 204`() {
        mockMvc.perform(
            delete("/api/v1/contacts/$TEST_NAME")
                .header("X-API-Key", apiKey)
        ).andExpect(status().isNoContent)
    }

    @Test
    @Order(5)
    fun `DELETE non-existent contact should return 404`() {
        mockMvc.perform(
            delete("/api/v1/contacts/$TEST_NAME")
                .header("X-API-Key", apiKey)
        ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
    }
}
