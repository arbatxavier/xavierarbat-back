package com.xavierarbat.xavierarbatback.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `GET projects should be public without API key`() {
        mockMvc.perform(get("/api/v1/projects"))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET projects with trailing slash should be public`() {
        mockMvc.perform(get("/api/v1/projects/"))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET project detail with invalid slug should return 404 with JSON error`() {
        mockMvc.perform(get("/api/v1/projects/non-existent-slug"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `POST projects without API key should return 401`() {
        mockMvc.perform(
            post("/api/v1/projects")
                .contentType("application/json")
                .content("{}")
        ).andExpect(status().isUnauthorized)
    }
}
