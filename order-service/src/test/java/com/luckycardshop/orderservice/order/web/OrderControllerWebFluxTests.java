package com.luckycardshop.orderservice.order.web;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;

import com.luckycardshop.orderservice.order.domain.Order;
import com.luckycardshop.orderservice.order.domain.OrderService;
import com.luckycardshop.orderservice.order.domain.OrderStatus;

import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class) //identifies a test class testing WebFlux components targetting OrderController
public class OrderControllerWebFluxTests {
	
	@Autowired
	private WebTestClient webClient; //WebClient variant with extra features to make testing RESTful services easier
	
	@MockitoBean
	private OrderService orderService; //mock OrderService to add to the Spring Context for this test
	
	@Test
	void whenCardNotAvailableThenRejectOrder() {
		
		//creating a test order request with the expected outcome being REJECTED
		var orderRequest = new OrderRequest("Name", 3);
		var expectedOrder = OrderService.buildRejectedOrder(orderRequest.name(), orderRequest.quantity());
		
		//We expect the behavior to be that the orderRequest will be the same as expectedOrder
		given(orderService.submitOrder(orderRequest.name(), orderRequest.quantity()))
			.willReturn(Mono.just(expectedOrder)); 
		
		//performs a POST request with the orderRequest
		//on the uri and we want to assert that actualOrder isn't null
		//and that the status of the order is equal to REJECTED
		webClient.post()
				.uri("/orders/")
				.bodyValue(orderRequest)
				.exchange().expectStatus().is2xxSuccessful()
				.expectBody(Order.class).value(actualOrder -> {
					assertThat(actualOrder).isNotNull();
					assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
				});
	}
}
