package com.luckycardshop.orderservice.card;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CardClientTests {
	private MockWebServer mockWebServer;
	private CardClient cardClient;
	
	@BeforeEach
	void setup() throws IOException {
		this.mockWebServer= new MockWebServer();	//creates a new mock web server
		this.mockWebServer.start(); //starts the web server 
		
		
		//tells the webClient that the source of the mock web server to use for tests that we set up before any tests
		var webClient = WebClient.builder()
								.baseUrl(mockWebServer.url("/").toString())
								.build();
		
		this.cardClient = new CardClient(webClient);
	}
	
	
	//after each test the mock web server is shut down
	@AfterEach
	void clean() throws IOException {
		this.mockWebServer.shutdown();
	}
	
	
	@Test
	void whenCardExistsThenReturnCard() {
		var cardName = "Pot of Greed";
		
		//response returned by mock server
		var mockResponse = new MockResponse()
								.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
								.setBody("""
										{
										"name": %s,
										"price": 9.90
										}
										""".formatted(cardName));
		
		//adds a mock response to the queue processed by the mock server
		mockWebServer.enqueue(mockResponse);
		
		Mono<Card> card = cardClient.getCardByName(cardName);
		
		//initializes a SetpVerifier object with the object returned by the Card Client
		StepVerifier.create(card)
					.expectNextMatches(c -> c.name().equals(cardName)) //we are asserting that the Card returned has the same name
					.verifyComplete(); //we verify that the reactive stream completed successfully
	}
}
