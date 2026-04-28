package com.xavierarbat.xavierarbatback.controller

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.jacksonObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectCrudTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var token: String

    companion object {
        private const val TEST_SLUG = "test-project-crud"
        private val objectMapper = jacksonObjectMapper()
    }

    @BeforeAll
    fun setup() {
        // Login
        val result = mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"username":"admin","password":"admin"}""")
        ).andExpect(status().isOk).andReturn()

        val body = objectMapper.readTree(result.response.contentAsByteArray)
        token = body.get("token").asText()

        // Seed tags
        listOf("ILLUSTRATION", "INK", "FAN_ART").forEach { tag ->
            mockMvc.perform(
                post("/api/v1/tags")
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"key": "$tag"}""")
            ) // ignore if already exists
        }
    }

    private fun authHeader() = "Bearer $token"

    private val createBody = """
        {
            "slug": "$TEST_SLUG",
            "date": "2026-04-26",
            "image": "/images/projects/test.jpg",
            "title": {"en": "Test Project", "es": "Proyecto de Prueba", "ca": "Projecte de Prova"},
            "description": {"en": "A test project.", "es": "Un proyecto de prueba.", "ca": "Un projecte de prova."},
            "content": {"en": "Test content.", "es": "Contenido de prueba.", "ca": "Contingut de prova."},
            "tags": ["ILLUSTRATION", "INK"],
            "imageDisplay": "COVER",
            "aspectRatio": "PORTRAIT",
            "altImages": ["/images/projects/test_01.jpg"]
        }
    """.trimIndent()

    @Test
    @Order(1)
    fun `POST project without token should return 401`() {
        mockMvc.perform(
            post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    @Order(2)
    fun `POST project with token should return 201`() {
        mockMvc.perform(
            delete("/api/v1/projects/$TEST_SLUG")
                .header("Authorization", authHeader())
        )

        mockMvc.perform(
            post("/api/v1/projects")
                .header("Authorization", authHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.slug").value(TEST_SLUG))
            .andExpect(jsonPath("$.title").value("Test Project"))
            .andExpect(jsonPath("$.tags").isArray)
            .andExpect(jsonPath("$.altImages[0]").value("/images/projects/test_01.jpg"))
    }

    @Test
    @Order(3)
    fun `GET created project should return it`() {
        mockMvc.perform(get("/api/v1/projects/$TEST_SLUG"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.slug").value(TEST_SLUG))
    }

    @Test
    @Order(4)
    fun `PUT project with token should update it`() {
        val updateBody = """
            {
                "title": {"en": "Updated Project", "es": "Proyecto Actualizado", "ca": "Projecte Actualitzat"},
                "tags": ["FAN_ART"]
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/projects/$TEST_SLUG")
                .header("Authorization", authHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Project"))
    }

    @Test
    @Order(5)
    fun `DELETE project with token should return 204`() {
        mockMvc.perform(
            delete("/api/v1/projects/$TEST_SLUG")
                .header("Authorization", authHeader())
        ).andExpect(status().isNoContent)
    }

    @Test
    @Order(6)
    fun `GET deleted project should return 404`() {
        mockMvc.perform(get("/api/v1/projects/$TEST_SLUG"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
    }
}
