package com.sidalitech.ApiGateway.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseResponse<T>(
    val status:String,
    val message:String,
    val data:T?=null,
    val token:String?=null,
)
