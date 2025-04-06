package com.luckycardshop.orderservice.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "luckycardshop")
public record ClientProperties(
	
	@NotNull
	URI catalogServiceUri
){}
