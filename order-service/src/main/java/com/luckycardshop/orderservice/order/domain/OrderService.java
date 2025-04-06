package com.luckycardshop.orderservice.order.domain;

import org.springframework.stereotype.Service;

import com.luckycardshop.orderservice.card.Card;
import com.luckycardshop.orderservice.card.CardClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	private final CardClient cardClient;
	
	public OrderService(OrderRepository orderRepository, CardClient cardClient) {
		this.orderRepository = orderRepository;	
		this.cardClient = cardClient;
	}
	
	public Flux<Order> getAllOrders() {
		return orderRepository.findAll();
	}
	
	
	public Mono<Order> submitOrder(String name, int quantity) {
		return cardClient.getCardByName(name)
				.map(card -> buildAcceptedOrder(card, quantity))
				.defaultIfEmpty(buildRejectedOrder(name, quantity))
				.flatMap(orderRepository::save);
	}
	
	public static Order buildRejectedOrder(String name, int quantity) {
		return Order.of(name, null, quantity, OrderStatus.REJECTED);
	}
	
	public static Order buildAcceptedOrder(Card card, int quantity) {
		return Order.of(card.name(), card.price(), quantity, OrderStatus.ACCEPTED);
	}
}
