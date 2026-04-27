package com.xavierarbat.xavierarbatback.controller

import com.xavierarbat.xavierarbatback.service.ImageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Images", description = "Upload, list, serve, and delete images")
class ImageController(private val imageService: ImageService) {

    @PostMapping("/{folder}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "Upload an image",
        description = "Uploads an image to the specified folder (projects, blogs, contacts, home). Max 20 MB. Allowed types: JPEG, PNG, WebP, GIF, SVG.",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "201", description = "Image uploaded, returns the public URL path"),
            ApiResponse(responseCode = "400", description = "Invalid file or folder"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun upload(
        @Parameter(description = "Target folder: projects, blogs, contacts, or home", example = "projects")
        @PathVariable folder: String,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val path = imageService.upload(folder, file)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("url" to path))
    }

    @GetMapping("/{folder}")
    @Operation(
        summary = "List images in a folder",
        description = "Returns a list of all image URL paths in the specified folder.",
        security = []
    )
    fun list(
        @Parameter(description = "Folder: projects, blogs, contacts, or home", example = "projects")
        @PathVariable folder: String
    ): List<String> {
        return imageService.listFiles(folder)
    }

    @DeleteMapping("/{folder}/{filename}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete an image",
        security = [SecurityRequirement(name = "ApiKeyAuth")],
        responses = [
            ApiResponse(responseCode = "204", description = "Image deleted"),
            ApiResponse(responseCode = "404", description = "Image not found"),
            ApiResponse(responseCode = "401", description = "Missing or invalid API key")
        ]
    )
    fun delete(
        @Parameter(description = "Folder", example = "projects") @PathVariable folder: String,
        @Parameter(description = "Filename", example = "berserk-main.jpg") @PathVariable filename: String
    ) {
        imageService.delete(folder, filename)
    }
}
