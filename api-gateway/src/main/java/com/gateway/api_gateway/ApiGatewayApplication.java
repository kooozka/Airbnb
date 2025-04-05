package com.gateway.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class ApiGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
		String appUri = "http://localhost:8081";
		return builder.routes()
				.route(p -> p
						.path("/rooms/**")
						.filters(f -> f.prefixPath("/api").addRequestHeader("Is_it", "working?"))
						.uri(appUri))
				.route(p -> p
						.method(HttpMethod.GET)
						.and()
						.path("/reservations/**")
						.filters(f -> f.prefixPath("/api").addRequestHeader("Is_it", "working?"))
						.uri(appUri))
				.route(p -> p
						.method(HttpMethod.POST)
						.and()
						.path("/reservations/room/{id}")
						.filters(f -> f.prefixPath("/api").addRequestHeader("Is_it", "working?"))
						.uri(appUri))
				.build();
	}
}
