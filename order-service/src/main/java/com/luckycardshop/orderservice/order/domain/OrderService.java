package com.luckycardshop.orderservice.order.domain;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	
	public OrderService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;	
	}
	
	public Flux<Order> getAllOrders() {
		return orderRepository.findAll();
	}
	
	
	public Mono<Order> submitOrder(String name, int quantity) {
		return Mono.just(buildRejectedOrder(name, quantity)).flatMap(orderRepository::save);
	}
	
	public static Order buildRejectedOrder(String name, int quantity) {
		return Order.of(name, null, quantity, OrderStatus.REJECTED);
	}
}
