package com.sidalitech.ApiGateway.config

import com.sidalitech.ApiGateway.service.UserService
import kotlinx.coroutines.runBlocking
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.*
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomUserDetailsServiceImpl(
    private val userService: UserService
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userService.getByUsername(username)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("Username not found: $username")))
            .map {
                // Map the user object to UserDetails object
                val roles = it?.roles?.map { it } ?: emptyList()
                User.withUsername(it?.username)
                    .password(it?.password)
                    .roles(*roles.toTypedArray()) // Add roles with 'ROLE_' prefix
                    .build()
            }
    }
}

//        return userRepository.findByUsername(username) // Reactive repository call
//            .map { user ->
//                User.withUsername(user.username)
//                    .password(user.password)
//                    .roles(*user.roles.toTypedArray())
//                    .build()
//            }
//@Component
//class CustomUserDetailsServiceImpl(
//    private val userService: UserService
//) : UserDetailsService {
//    override fun loadUserByUsername(username: String?): UserDetails {
//
//        val context = SecurityContextHolder.getContext()
//        val obj = runBlocking {
//            try {
//                SecurityContextHolder.setContext(context)
//                userService.getByUsername(username ?: "")
//                    ?: throw UsernameNotFoundException("Username not found: $username")
//            } finally {
//                SecurityContextHolder.clearContext()
//            }
//        }
//        println("After: ${SecurityContextHolder.getContext().authentication}")
//
//        return User.builder()
//            .username(obj.username)
//            .password(obj.password)
//            .roles(*(obj.roles.map { it }.toTypedArray() ?: arrayOf()))
//            .build()
//    }
//}
