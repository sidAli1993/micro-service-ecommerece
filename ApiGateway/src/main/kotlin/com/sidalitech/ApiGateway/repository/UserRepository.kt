package com.sidalitech.ApiGateway.repository

import com.sidalitech.ApiGateway.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository :ReactiveCrudRepository<User,String> {
    fun findByUsername(username:String) : Mono<User?>
}