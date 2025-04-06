package com.luckycardshop.orderservice.card;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class CardClient {
	private static final String CARD_ROOT_API = "/cards/";
	private final WebClient webClient;
	
	public CardClient(WebClient webClient) {
		this.webClient = webClient; //this is the WebClient that was configured in the config package
	}
	
	public Mono<Card> getCardByName(String cardName) {
		return webClient
				.get() //the request is using the get() method since we want the information
				.uri(CARD_ROOT_API + cardName) //the target uri will be in catalog service but /cards/card name
				.retrieve() //this method sends the request and retrieves the information
				.bodyToMono(Card.class); //transforms what we got back from the get as a Mono<Card>
	}
	
}
