package com.luckycardshop.orderservice.order.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckycardshop.orderservice.order.domain.Order;
import com.luckycardshop.orderservice.order.domain.OrderService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
public class OrderController {
	private final OrderService orderService;
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@GetMapping //gets the currently authenticated user as a parameter in a JWT
	public Flux<Order> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
		return orderService.getAllOrders(jwt.getSubject()); //extracts the userId from the JWT to use
	}
	
	@PostMapping
	public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
		return orderService.submitOrder(orderRequest.name(), orderRequest.quantity());
	}
}
