package com.sidalitech.ApiGateway.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val userDetailsService: CustomUserDetailsServiceImpl, // Updated for reactive service
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expiration}") private val jwtExpiration: Long
) {

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    /**
     * Generate a JWT token for the given authentication object.
     */
    fun generateToken(authentication: Authentication): String {
        val roles = authentication.authorities.map { authority ->
            authority.authority.removePrefix("ROLE_") // Remove prefix if already present
        }

        return Jwts.builder()
            .setSubject(authentication.name)
            .claim("roles", roles) // Add roles to the token without prefix
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * Validate the given JWT token.
     */
    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
            !claims.expiration.before(Date()) // Ensure token is not expired
        } catch (ex: Exception) {
            println("Token Validation Failed: ${ex.message}")
            false
        }
    }

    /**
     * Get Authentication object from a JWT token.
     * This method retrieves user details reactively.
     */
    fun getAuthentication(token: String): Mono<Authentication> {
        val username = getUsernameFromToken(token)

        // Parse claims from the token
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        // Extract roles from claims
        val roles = claims["roles"] as? List<*> ?: emptyList<Any>()

        // Map roles to SimpleGrantedAuthority
        val authorities = roles.mapNotNull { role ->
            role?.toString()?.let { SimpleGrantedAuthority(it) }
        }

        println("Extracted Username: $username")
        println("Extracted Roles: $roles")
        println("Mapped Authorities: $authorities")

        // Fetch user details and return authentication
        return userDetailsService.findByUsername(username)
            .map { userDetails ->
                UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
                )
            }
    }

    fun getRolesFromToken(token: String): List<SimpleGrantedAuthority> {
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        val roles = claims["roles"] as? List<*> ?: emptyList<Any>()

        return roles.mapNotNull { role ->
            role?.toString()?.let { SimpleGrantedAuthority(it) }
        }
    }

    /**
     * Extract the username from a JWT token.
     */
    fun getUsernameFromToken(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun resolveToken(request: ServerHttpRequest): String? {
        val bearerToken = request.headers.getFirst("Authorization")
        return if (!bearerToken.isNullOrBlank() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7) // Remove "Bearer " prefix
        } else {
            null
        }
    }
}