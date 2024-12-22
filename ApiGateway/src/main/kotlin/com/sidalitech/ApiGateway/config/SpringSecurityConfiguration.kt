package com.sidalitech.ApiGateway.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SpringSecurityConfiguration(
//    private val jwtAuthenticationWebFilter: JwtAuthenticationWebFilter

) {

    @Autowired
    lateinit var customUserDetailsServiceImpl: CustomUserDetailsServiceImpl

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Value("\$spring.data.mongodb.uri")
    lateinit var mongoUri: String

    @Value("\$spring.data.mongodb.database")
    lateinit var databaseName: String

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() } // Disable CSRF
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/public/**").permitAll() // Public endpoints
                    .pathMatchers("/admin/**").hasRole("ADMIN")
                    .anyExchange().authenticated() // All other endpoints require authentication
            }
            .authenticationManager(reactiveAuthenticationManager()) // Set reactive authentication manager
            .httpBasic { it.authenticationEntryPoint(HttpBasicServerAuthenticationEntryPoint()) } // HTTP Basic Auth
            .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION) // Add JWT filter
            .build()
    }

    @Bean
    fun jwtAuthenticationConverter(): ServerAuthenticationConverter {
        return ServerAuthenticationConverter { exchange ->
            val token = jwtTokenProvider.resolveToken(exchange.request)
            if (token != null && jwtTokenProvider.validateToken(token)) {
                val username = jwtTokenProvider.getUsernameFromToken(token)
                val authorities = jwtTokenProvider.getRolesFromToken(token) // Extract roles

                Mono.justOrEmpty(UsernamePasswordAuthenticationToken(username, null, authorities))
            } else {
                println("Invalid or Missing Token")
                Mono.empty()
            }
        }
    }


    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

//    @Bean
//    fun jwtAuthenticationWebFilter(): AuthenticationWebFilter {
//        val jwtAuthenticationWebFilter = AuthenticationWebFilter(reactiveAuthenticationManager())
//        jwtAuthenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter())
//        jwtAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange())
//        return jwtAuthenticationWebFilter
//    }
@Bean
fun jwtAuthenticationWebFilter(): AuthenticationWebFilter {
    val jwtAuthenticationWebFilter = AuthenticationWebFilter(reactiveAuthenticationManager())
    jwtAuthenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter())
    // Apply JWT filter for multiple protected paths
    jwtAuthenticationWebFilter.setRequiresAuthenticationMatcher(
        ServerWebExchangeMatchers.pathMatchers("/admin/**", "/customer/**", "/seller/**","/v1/**")
    )
    return jwtAuthenticationWebFilter
}

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager {
        // Custom AuthenticationManager that bypasses password checking
        return object : ReactiveAuthenticationManager {
            override fun authenticate(authentication: Authentication): Mono<Authentication> {
                // If JWT token authentication is provided, don't check password (bypass password check)
                if (authentication is UsernamePasswordAuthenticationToken) {
                    val username = authentication.name
                    val authorities = authentication.authorities
                    // Return authenticated token with username and authorities
                    return Mono.just(UsernamePasswordAuthenticationToken(username, null, authorities))
                }
                return Mono.empty()
            }
        }
    }
}
