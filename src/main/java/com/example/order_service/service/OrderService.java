package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.model.Order;
import com.example.order_service.model.Product;
import com.example.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repo;

    @Autowired
    private ProductClient productClient;

    @Transactional
    public Order placeOrder(Order order) {

        // Call product-service
        Product product = productClient.getProduct(order.getProductId());

        // Validate stock
        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        productClient.reduceStock(order.getProductId(), order.getQuantity());

        // Save order
        return repo.save(order);
    }

}