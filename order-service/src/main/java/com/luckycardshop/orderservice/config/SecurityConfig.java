package com.luckycardshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@EnableWebFluxSecurity
public class SecurityConfig {

		@Bean
		SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
			return http.authorizeExchange(exchange -> exchange.anyExchange().authenticated())
						//adjust the line right below just like in Catalog Service because not using Customizer is now deprecated
						.oauth2ResourceServer(OAuth2ResourceServerSpec -> OAuth2ResourceServerSpec.jwt(Customizer.withDefaults())) 
						.requestCache(requestCacheSpec -> requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
						.csrf(ServerHttpSecurity.CsrfSpec::disable)
						.build();
		}
}
