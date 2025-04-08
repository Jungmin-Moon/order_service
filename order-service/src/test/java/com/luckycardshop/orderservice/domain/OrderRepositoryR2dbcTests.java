package com.luckycardshop.orderservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.luckycardshop.orderservice.config.DataConfig;
import com.luckycardshop.orderservice.order.domain.OrderRepository;
import com.luckycardshop.orderservice.order.domain.OrderService;
import com.luckycardshop.orderservice.order.domain.OrderStatus;

import reactor.test.StepVerifier;

@DataR2dbcTest //identifies a test class that focuses on R2DBC components
@Import(DataConfig.class) //imports the R2DBC config needed to enable auditing
@Testcontainers //activates auto startup and clean up of test containers
public class OrderRepositoryR2dbcTests {
	
	//tells JUnit that this is the container that will house a PostgreSQL container for testing
	@Container
	static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	//overwrites R2DBC and Flyway config to point to the test instance
	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
		registry.add("spring.r2dbc.username", postgresql::getUsername);
		registry.add("spring.r2dbc.password", postgresql::getPassword);
		registry.add("spring.flyway.url", postgresql::getJdbcUrl);
	}
	
	//builds an R2DBC connection string b/c TestContainers doesn't provide one out of the box like JDBC
	private static String r2dbcUrl() {
		return String.format("r2dbc:postgresql://%s:%s/%s", 
				postgresql.getContainerIpAddress(), //will need to see latest way for this comapred to book
				postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
				postgresql.getDatabaseName());
	}
	
	
	@Test
	void createRejectedOrder() {
		var rejectedOrder = OrderService.buildRejectedOrder("Name", 3);
		
		StepVerifier.create(orderRepository.save(rejectedOrder))
					.expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))
					.verifyComplete();
	}
}
