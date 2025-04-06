package com.luckycardshop.orderservice.order.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders") //order is a reserved word in sql so this annotation solves the issue when mapping
public record Order( 
	
	@Id
	Long id,
	
	String name,
	Double price,
	Integer quantity,
	OrderStatus status,
	
	@CreatedDate
	Instant createdDate,
	
	@LastModifiedDate
	Instant lastModifiedDate,
	
	@Version
	int version
){
	public static Order of(String cardName, Double cardPrice, Integer quantity, OrderStatus status) {
		return new Order(null, cardName, cardPrice, quantity, status, null, null, 0);
	}
}
