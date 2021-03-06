package com.example.demo.Routing;

import com.example.demo.Handler.ProductHandler;
import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Router {
    @Bean
    RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return route(GET("/products").and(accept(MediaType.APPLICATION_JSON)), handler::getAllProducts)
                .andRoute(POST("/products").and(accept(MediaType.APPLICATION_JSON)), handler::saveProduct)
                .andRoute(DELETE("/products").and(accept(MediaType.APPLICATION_JSON)), handler::deleteAllProducts)
                .andRoute(GET("/products/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::getProduct)
                .andRoute(PUT("/products/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::updateProduct)
                .andRoute(DELETE("/products/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::deleteProduct);
    }

    @Bean
    CommandLineRunner init(ProductRepository productRepository) {
        return args -> {
            Flux<Product> productFlux = Flux.just(
                    new Product(null, "Big Latte", 2.99),
                    new Product(null, "Big Decaf", 2.49),
                    new Product(null, "Green Tea", 1.99)
            ).flatMap(productRepository::save);

            productFlux.thenMany(productRepository.findAll()).subscribe(System.out::println);
        };
    }
}
