package com.sidalitech.ApiGateway.config

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtForwardingFilter : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        // Get the token from the incoming request header (assumes the request is already authenticated)
        val token = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        // Check if token is available and it starts with "Bearer "
        if (token != null && token.startsWith("Bearer ")) {
            // Forward the token in the header to the microservice
            exchange.request.mutate()
                .header(HttpHeaders.AUTHORIZATION, token) // Forward the JWT token
                .build()
        }

        // Proceed with the request
        return chain.filter(exchange)
    }

    override fun getOrder(): Int {
        return -1 // Make sure the filter runs before the request is handled
    }
}
