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
class BlogCrudTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Value("\${app.api-key}")
    private lateinit var apiKey: String

    companion object {
        private const val TEST_SLUG = "test-blog-crud"
    }

    private val createBody = """
        {
            "slug": "$TEST_SLUG",
            "date": "2026-04-26",
            "title": {"en": "Test Blog", "es": "Blog de Prueba", "ca": "Blog de Prova"},
            "description": {"en": "A test blog post.", "es": "Un blog de prueba.", "ca": "Un blog de prova."},
            "content": {"en": "Test content.", "es": "Contenido de prueba.", "ca": "Contingut de prova."}
        }
    """.trimIndent()

    @Test
    @Order(1)
    fun `POST blog without API key should return 401`() {
        mockMvc.perform(
            post("/api/v1/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        ).andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
    }

    @Test
    @Order(2)
    fun `POST blog with API key should return 201`() {
        // Clean up first in case previous test run left data
        mockMvc.perform(
            delete("/api/v1/blogs/$TEST_SLUG")
                .header("X-API-Key", apiKey)
        )

        mockMvc.perform(
            post("/api/v1/blogs")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.slug").value(TEST_SLUG))
            .andExpect(jsonPath("$.title").value("Test Blog"))
    }

    @Test
    @Order(3)
    fun `GET created blog should return it`() {
        mockMvc.perform(get("/api/v1/blogs/$TEST_SLUG"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.slug").value(TEST_SLUG))
    }

    @Test
    @Order(4)
    fun `PUT blog with API key should update it`() {
        val updateBody = """
            {
                "title": {"en": "Updated Blog", "es": "Blog Actualizado", "ca": "Blog Actualitzat"}
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/blogs/$TEST_SLUG")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Blog"))
    }

    @Test
    @Order(5)
    fun `PUT blog without API key should return 401`() {
        mockMvc.perform(
            put("/api/v1/blogs/$TEST_SLUG")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"title": {"en": "Hacked"}}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    @Order(6)
    fun `DELETE blog with API key should return 204`() {
        mockMvc.perform(
            delete("/api/v1/blogs/$TEST_SLUG")
                .header("X-API-Key", apiKey)
        ).andExpect(status().isNoContent)
    }

    @Test
    @Order(7)
    fun `GET deleted blog should return 404`() {
        mockMvc.perform(get("/api/v1/blogs/$TEST_SLUG"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
    }

    @Test
    @Order(8)
    fun `DELETE blog without API key should return 401`() {
        mockMvc.perform(delete("/api/v1/blogs/any-slug"))
            .andExpect(status().isUnauthorized)
    }
}
