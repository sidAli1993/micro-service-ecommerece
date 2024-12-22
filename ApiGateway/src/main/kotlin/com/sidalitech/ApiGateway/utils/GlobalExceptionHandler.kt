package com.sidalitech.ApiGateway.utils

import com.sidalitech.ApiGateway.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    // Handle generic exceptions
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<BaseResponse<Any>> {
        return buildResponse(
            status = "error",
            message = "An unexpected error occurred ${ex.localizedMessage}",
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<BaseResponse<Any>> {
        return buildResponse(
            status = "error",
            message = ex.localizedMessage ?: "Invalid input provided",
            httpStatus = HttpStatus.BAD_REQUEST,
            data = null
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<BaseResponse<Any>> {
        return buildResponse(
            status = "error",
            message = ex.localizedMessage ?: "Invalid input provided",
            httpStatus = HttpStatus.BAD_REQUEST,
        )
    }
}
