package com.xavierarbat.xavierarbatback.service

import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class ImageService(
    @Value("\${app.uploads.path:/app/uploads}")
    private val uploadsPath: String,

    @Value("\${app.uploads.max-size:20971520}")
    private val maxFileSize: Long,

    @Value("\${app.uploads.allowed-types:image/jpeg,image/png,image/webp,image/gif,image/svg+xml}")
    private val allowedTypesRaw: String
) {

    private val allowedTypes: Set<String> by lazy { allowedTypesRaw.split(",").map { it.trim() }.toSet() }

    private val validFolders = setOf("projects", "blogs", "contacts", "home")

    @PostConstruct
    fun init() {
        try {
            validFolders.forEach { folder ->
                Files.createDirectories(Paths.get(uploadsPath, folder))
            }
        } catch (e: Exception) {
            logger.warn("Could not create upload directories at '$uploadsPath': ${e.message}. Uploads will fail until the directory is available.")
        }
    }

    private companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(ImageService::class.java)
    }

    fun upload(folder: String, file: MultipartFile): String {
        validateFolder(folder)
        validateFile(file)

        val originalFilename = file.originalFilename
            ?: throw IllegalArgumentException("File must have a name")

        val safeName = sanitizeFilename(originalFilename)
        val targetDir = Paths.get(uploadsPath, folder)
        Files.createDirectories(targetDir)

        var targetPath = targetDir.resolve(safeName)

        // If file already exists, add a numeric suffix
        if (Files.exists(targetPath)) {
            val nameWithoutExt = safeName.substringBeforeLast(".")
            val ext = safeName.substringAfterLast(".", "")
            var counter = 1
            while (Files.exists(targetPath)) {
                val newName = if (ext.isNotEmpty()) "${nameWithoutExt}_$counter.$ext" else "${nameWithoutExt}_$counter"
                targetPath = targetDir.resolve(newName)
                counter++
            }
        }

        file.inputStream.use { input ->
            Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }

        return "/uploads/$folder/${targetPath.fileName}"
    }

    fun listFiles(folder: String): List<String> {
        validateFolder(folder)
        val dir = Paths.get(uploadsPath, folder)
        if (!Files.exists(dir)) return emptyList()

        return Files.list(dir)
            .filter { Files.isRegularFile(it) }
            .map { "/uploads/$folder/${it.fileName}" }
            .sorted()
            .toList()
    }

    fun delete(folder: String, filename: String) {
        validateFolder(folder)
        val safeName = sanitizeFilename(filename)
        val filePath = Paths.get(uploadsPath, folder, safeName)

        if (!Files.exists(filePath)) {
            throw ResourceNotFoundException("File not found: $folder/$safeName")
        }
        Files.delete(filePath)
    }

    fun resolve(folder: String, filename: String): Path {
        validateFolder(folder)
        val safeName = sanitizeFilename(filename)
        val filePath = Paths.get(uploadsPath, folder, safeName)
        if (!Files.exists(filePath)) {
            throw ResourceNotFoundException("File not found: $folder/$safeName")
        }
        return filePath
    }

    private fun validateFolder(folder: String) {
        if (folder !in validFolders) {
            throw IllegalArgumentException("Invalid folder '$folder'. Allowed: $validFolders")
        }
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }
        if (file.size > maxFileSize) {
            throw IllegalArgumentException("File exceeds maximum size of ${maxFileSize / 1024 / 1024} MB")
        }
        val contentType = file.contentType ?: throw IllegalArgumentException("File content type is unknown")
        if (contentType !in allowedTypes) {
            throw IllegalArgumentException("File type '$contentType' is not allowed. Allowed: $allowedTypes")
        }
    }

    /**
     * Removes path traversal characters and keeps only safe filename chars.
     */
    private fun sanitizeFilename(filename: String): String {
        val name = Paths.get(filename).fileName.toString()
        val sanitized = name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        if (sanitized.isBlank() || sanitized.startsWith(".")) {
            throw IllegalArgumentException("Invalid filename: $filename")
        }
        return sanitized
    }
}
