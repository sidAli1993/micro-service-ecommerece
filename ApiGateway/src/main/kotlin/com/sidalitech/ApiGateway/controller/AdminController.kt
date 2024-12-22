package com.sidalitech.ApiGateway.controller

import com.sidalitech.ApiGateway.model.BaseResponse
import com.sidalitech.ApiGateway.model.User
import com.sidalitech.ApiGateway.service.UserService
import com.sidalitech.ApiGateway.utils.buildResponse
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/admin")
class AdminController(
    private val userService: UserService,
) {

    @GetMapping("/users")
    fun getAllUsers(@RequestHeader("Authorization") token:String): Mono<ResponseEntity<BaseResponse<Any>>> {
        return userService.getUsers() // This returns Flux<User>
            .collectList() // Collect all users into a List
            .flatMap { users ->
                if (users.isEmpty()) {
                    return@flatMap Mono.error<ResponseEntity<BaseResponse<Any>>>(RuntimeException("No users found"))
                }
                // If users are found, return the response
                Mono.just(
                    buildResponse(
                        "success",
                        "users found successfully",
                        HttpStatus.OK,
                        data = users
                    )
                )
            }
    }
}