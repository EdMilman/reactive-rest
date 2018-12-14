package com.example.demo.Handler;

import com.example.demo.Model.Product;
import com.example.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;

@Component
public class ProductHandler {
    private ProductService service;

    @Value("Product")
    String className;

    public ProductHandler(ProductService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Product.class);
    }

    public Mono<ServerResponse> getProduct(ServerRequest request) {
        String productId = request.pathVariable("id");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findById(productId), Product.class)
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.save(productMono), Product.class);
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.update(productMono, id), Product.class)
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String productId = request.pathVariable("id");
        return ServerResponse.ok()
                .build(service.delete(productId))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> deleteAllProducts(ServerRequest request) {
        return ServerResponse.ok().build(service.deleteAll());
    }

}
