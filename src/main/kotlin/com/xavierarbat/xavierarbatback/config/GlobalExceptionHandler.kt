package com.xavierarbat.xavierarbatback.config

import com.xavierarbat.xavierarbatback.dto.ErrorResponse
import com.xavierarbat.xavierarbatback.exception.ResourceNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found", request)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(ex: NoResourceFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.NOT_FOUND, "Path not found: ${request.requestURI}", request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return buildResponse(HttpStatus.BAD_REQUEST, errors, request)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(ex: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method ${ex.method} not supported", request)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request", request)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error on ${request.method} ${request.requestURI}", ex)
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request)
    }

    private fun buildResponse(status: HttpStatus, message: String, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(error)
    }
}
