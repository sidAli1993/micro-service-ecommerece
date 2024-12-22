package com.sidalitech.ApiGateway.service

import com.sidalitech.ApiGateway.model.User
import com.sidalitech.ApiGateway.repository.UserRepository
import kotlinx.coroutines.flow.toList
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository
) {
    // Create a new user
    fun createUser(user: User): Mono<User> {
        return userRepository.save(user)
    }

    // Get all users
    fun getUsers(): Flux<User> {
        return userRepository.findAll()
    }

    // Get a user by username
    fun getByUsername(username: String): Mono<User?> {
        return userRepository.findByUsername(username)
    }

    // Get a user by ID
    fun getById(id: String): Mono<User?> {
        return userRepository.findById(id)
    }

    // Delete a user by ID
    fun deleteById(id: String): Mono<Void> {
        return userRepository.deleteById(id)
    }
}
