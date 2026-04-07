package com.example.order_service.client;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PostMapping("/products/reserve")
    void reserve(OrderRequest request);

    @PostMapping("/products/confirm")
    void confirm(OrderRequest request);

    @PostMapping("/products/release")
    void release(OrderRequest request);

    @GetMapping("/products/{id}")
    Product getProduct(@PathVariable Long id);
}