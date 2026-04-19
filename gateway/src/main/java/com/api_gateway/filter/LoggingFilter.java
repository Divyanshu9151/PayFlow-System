package com.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("➡️ Incoming Request: {} {}", method, path);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {

                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode().value();

                    log.info("⬅️ Response: {} {} | Status: {} | Time: {} ms",
                            method, path, statusCode, duration);
                }));
    }

    @Override
    public int getOrder() {
        return -1; // run early
    }
}