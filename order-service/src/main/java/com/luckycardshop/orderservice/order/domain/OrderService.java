package com.luckycardshop.orderservice.order.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckycardshop.orderservice.card.Card;
import com.luckycardshop.orderservice.card.CardClient;
import com.luckycardshop.orderservice.order.event.OrderAcceptedMessage;
import com.luckycardshop.orderservice.order.event.OrderDispatchedMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	
	private final OrderRepository orderRepository;
	private final CardClient cardClient;
	private final StreamBridge streamBridge;
	
	public OrderService(OrderRepository orderRepository, CardClient cardClient, StreamBridge streamBridge) {
		this.orderRepository = orderRepository;	
		this.cardClient = cardClient;
		this.streamBridge = streamBridge;
	}
	
	public Flux<Order> getAllOrders() {
		return orderRepository.findAll();
	}
	
	@Transactional
	public Mono<Order> submitOrder(String name, int quantity) {
		return cardClient.getCardByName(name)
				.map(card -> buildAcceptedOrder(card, quantity))
				.defaultIfEmpty(buildRejectedOrder(name, quantity))
				.flatMap(orderRepository::save)
				.doOnNext(this::publishOrderAcceptedEvent);
	}
	
	public static Order buildRejectedOrder(String name, int quantity) {
		return Order.of(name, null, quantity, OrderStatus.REJECTED);
	}
	
	public static Order buildAcceptedOrder(Card card, int quantity) {
		return Order.of(card.name(), card.price(), quantity, OrderStatus.ACCEPTED);
	}
	
	public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
		return flux.flatMap(message -> orderRepository.findById(message.orderId()))
					.map(this::buildDispatchedOrder)
					.flatMap(orderRepository::save);
				
	}
	
	private Order buildDispatchedOrder(Order existingOrder) {
		return new Order(
				existingOrder.id(),
				existingOrder.name(),
				existingOrder.price(),
				existingOrder.quantity(),
				OrderStatus.DISPATCHED,
				existingOrder.createdDate(),
				existingOrder.lastModifiedDate(),
				existingOrder.version()
				);
	}
	
	private void publishOrderAcceptedEvent(Order order) {
		if (!order.status().equals(OrderStatus.ACCEPTED)) {
			return;
		} 
		
		var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
		
		log.info("Sending order accepted event with id: {}", order.id());
		
		var result = streamBridge.send("acceptedOrder-out-0", orderAcceptedMessage);
		
		log.info("Result of sending data for order with id {}: {}", order.id(), result);
	}
}
