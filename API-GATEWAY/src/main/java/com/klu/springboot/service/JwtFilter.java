package com.klu.springboot.service;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        boolean protectedPath =
                path.startsWith("/books")
                || path.startsWith("/payments");

        if (!protectedPath) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest()
                .getHeaders().getFirst("Authorization");

        if (authorization == null
                || !authorization.startsWith("Bearer ")
                || !JwtUtil.isValid(authorization.substring(7))) {

            exchange.getResponse().setStatusCode(
                    HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}