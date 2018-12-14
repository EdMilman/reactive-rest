package com.example.demo;

import com.example.demo.Handler.ProductHandler;
import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@AutoConfigureWebTestClient
public class DemoApplicationTests {

    @Autowired
    private WebTestClient client;

    private List<Product> expectedList;

    @Autowired
    private ProductRepository repository;

    @Autowired
    ProductHandler handler;


    @Test
    public void testGetAllProducts() {
        client.get()
                .uri("/products")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedList);
    }

    @Test
    public void testProductFound(){
        Product expectedProduct = expectedList.get(0);
        client.get()
                .uri("/{id}", expectedProduct.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .isEqualTo(expectedProduct);
    }

    @Test
    public void testProductInvalidNotFound(){
        client.get()
                .uri("/aaa")
                .exchange()
                .expectStatus()
                .isNotFound();
    }


}
