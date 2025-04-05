package com.luckycardshop.orderservice.order.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
	
	@NotBlank(message = "The name of the card must be definied.")
	String name,
	
	@NotNull(message = "The quantity must be defined.")
	@Min(value = 1, message = "You must order at least 1 item.")
	@Max(value = 3, message = "No more than 3 copies per card.")
	Integer quantity
		
) {}
