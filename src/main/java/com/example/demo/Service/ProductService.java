package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductService<T> {
    private ReactiveCrudRepository repository;

    public ProductService(ReactiveCrudRepository productRepository) {
        this.repository = productRepository;
    }

    public Flux<T> findAll() {
        return repository.findAll();
    }

    public Mono<T> findById(String id) {
        return repository.findById(id);
    }

    public Mono<T> save(Mono<Product> productMono) {
        return productMono.flatMap(product -> repository.save(product));
    }

    public Mono<T> update(Mono<T> productMono, String id) {
        Mono<T> existing = repository.findById(id);
        return existing.zipWith(productMono)
                .flatMap(tuple -> {
                    Product current = (Product) tuple.getT1();
                    Product newProduct = (Product) tuple.getT2();
                    current.setName(newProduct.getName());
                    current.setPrice(newProduct.getPrice());
                    return repository.save(current);
                });
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }
}
