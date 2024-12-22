package com.sidalitech.ApiGateway.model.dto

import jakarta.validation.constraints.NotNull

data class DTOLogin(
    @field:NotNull(message = "username is empty")
    val username:String,
    @field:NotNull(message = "password is empty")
    val password:String
)
