package com.luckycardshop.orderservice.card;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

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
				.bodyToMono(Card.class) //transforms what we got back from the get as a Mono<Card>
				.timeout(Duration.ofSeconds(3), Mono.empty()) //creates a timeout for the GET request
				.onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
				//if the GET does time out return an empty Mono<Card> for now
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100))) //we want the request to retry 3 times with a interval of 100
				//milliseconds from the first time and we want to include the timeout as well since we need to see if the retry 
				//also times out, this action will also keep doing it if we didnt set the onErrorResume to include responses like 404
				.onErrorResume(Exception.class, exception -> Mono.empty()); //this will catch any other exception
				//and return a empty Mono object
	}
	
}
