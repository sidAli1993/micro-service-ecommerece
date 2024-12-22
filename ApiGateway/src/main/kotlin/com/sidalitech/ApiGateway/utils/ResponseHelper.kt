package com.sidalitech.ApiGateway.utils

import com.sidalitech.ApiGateway.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> successResponse(data: T, message: String = "Success"): BaseResponse<T> {
    return BaseResponse(
        status = "success",
        message = message,
        data = data
    )
}

fun <T> errorResponse(errorMessage: String, data: T? = null): BaseResponse<T> {
    return BaseResponse(
        status = "error",
        message = errorMessage,
        data = data
    )
}

fun <T> buildResponse(
    status: String,
    message: String,
    httpStatus: HttpStatus,
    token: String?=null,
    data: T?=null,
): ResponseEntity<BaseResponse<T>> {
    return ResponseEntity(
        BaseResponse(status = status, message = message,data= data, token =token),
        httpStatus
    )
}