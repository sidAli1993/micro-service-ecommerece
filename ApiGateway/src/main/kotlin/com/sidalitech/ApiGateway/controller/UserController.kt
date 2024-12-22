package com.sidalitech.ApiGateway.controller

import com.fasterxml.jackson.databind.ser.Serializers.Base
import com.sidalitech.ApiGateway.model.BaseResponse
import com.sidalitech.ApiGateway.model.User
import com.sidalitech.ApiGateway.model.dto.DTOLogin
import com.sidalitech.ApiGateway.service.UserService
import com.sidalitech.ApiGateway.utils.buildResponse
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class UserController(
    private val userService: UserService,
) {
    private val passwordEncoder= BCryptPasswordEncoder()

    @GetMapping("/dummy")
    fun getDummy():ResponseEntity<BaseResponse<Any>>{
        return buildResponse("success",
            "called api",
            HttpStatus.OK,
            null,
            "Hello world")
    }
}