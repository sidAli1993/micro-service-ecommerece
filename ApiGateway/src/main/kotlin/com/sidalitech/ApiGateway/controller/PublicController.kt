package com.sidalitech.ApiGateway.controller

import com.sidalitech.ApiGateway.config.JwtTokenProvider
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
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/public")
class PublicController(
    private val userService: UserService,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val passwordEncoder = BCryptPasswordEncoder()

    @PostMapping("/user")
    fun createUser(@Valid @RequestBody user: User): Mono<ResponseEntity<BaseResponse<Any>>> {
        // Simple manual validation check for user input
        if (user.username.isBlank()) {
            return Mono.just(
                buildResponse(
                    "error",
                    "Username is required",
                    HttpStatus.BAD_REQUEST
                )
            )
        }
        if (user.password.isBlank()) {
            return Mono.just(
                buildResponse(
                    "error",
                    "Password is required",
                    HttpStatus.BAD_REQUEST
                )
            )
        }

        // If no validation errors, proceed with user creation
        val oldPassword = user.password
        user.password = passwordEncoder.encode(user.password)

        return userService.createUser(user)
            .flatMap { result ->
                // Asynchronous authentication using ReactiveAuthenticationManager
                val authorities = user.roles.map { role -> SimpleGrantedAuthority(role) } // Convert roles to authorities

                val authenticationToken = UsernamePasswordAuthenticationToken(user.username, oldPassword,authorities)
                authenticationManager.authenticate(authenticationToken) // Will return Mono<Authentication>
                    .flatMap { authentication ->
                        val token = if (authentication.isAuthenticated) {
                            jwtTokenProvider.generateToken(authentication)
                        } else {
                            null
                        }

                        Mono.just(
                            buildResponse(
                                "success",
                                "User successfully created",
                                HttpStatus.CREATED,
                                data = result,
                                token = token
                            )
                        )
                    }
            }
    }

    @PostMapping("/login")
    fun doLogin(@Valid @RequestBody dtoLogin: DTOLogin): Mono<ResponseEntity<BaseResponse<Any>>> {
        // Simple manual validation check for login data
        if (dtoLogin.username.isNullOrBlank()) {
            return Mono.just(
                buildResponse<Any>(
                    "error",
                    "Username is required",
                    HttpStatus.BAD_REQUEST
                )
            )
        }
        if (dtoLogin.password.isBlank()) {
            return Mono.just(
                buildResponse<Any>(
                    "error",
                    "Password is required",
                    HttpStatus.BAD_REQUEST
                )
            )
        }

        return userService.getByUsername(dtoLogin.username)
            .flatMap { user ->
                // Asynchronous authentication using ReactiveAuthenticationManager
                val authorities = user?.roles?.map { role -> SimpleGrantedAuthority(role) } // Convert roles to authorities

                val authenticationToken = UsernamePasswordAuthenticationToken(dtoLogin.username, dtoLogin.password,authorities)
                authenticationManager.authenticate(authenticationToken) // Will return Mono<Authentication>
                    .flatMap { authentication ->
                        // Check if authentication is successful
                        if (authentication.isAuthenticated) {
                            val token = jwtTokenProvider.generateToken(authentication)
                            Mono.just(
                                buildResponse<Any>(
                                    "success",
                                    "User login successfully",
                                    HttpStatus.OK,
                                    data = user,  // User will be returned as part of the response
                                    token = token
                                )
                            )
                        } else {
                            Mono.just(
                                buildResponse<Any>(
                                    "error",
                                    "Invalid credentials",
                                    HttpStatus.UNAUTHORIZED
                                )
                            )
                        }
                    }
            }
            .switchIfEmpty(Mono.just(buildResponse<Any>(
                "error",
                "User not found",
                HttpStatus.NOT_FOUND
            )))
    }
}
