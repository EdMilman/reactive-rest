package com.example.demo.Handler;

import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductHandler {
    private ProductRepository repository;

    public ProductHandler(ProductRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        Flux<Product> products = repository.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(products, Product.class);
    }

    public Mono<ServerResponse> getProduct(ServerRequest request) {
        Mono<Product> product = repository.findById(request.pathVariable("id"));
        return product.flatMap(prod ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(prod))
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(product ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(repository.save(product), Product.class));
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> existing = repository.findById(id);
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.zipWith(existing,
                (product, existingProduct) ->
                        new Product(existingProduct.getId(), existingProduct.getName(), existingProduct.getPrice())
        ).flatMap(product -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repository.save(product), Product.class)).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        return repository.findById(request.pathVariable("id")).flatMap(product -> ServerResponse.ok().build(repository.delete(product)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteAllProducts(ServerRequest request) {
        return ServerResponse.ok().build(repository.deleteAll());
    }

}
