package com.example.order_service.controller;

import com.example.order_service.dto.OrderSummary;
import com.example.order_service.model.Order;
import com.example.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping
    public OrderSummary placeOrder(@RequestBody Order order) {
        return service.placeOrder(order);
    }

    @GetMapping("/getOrders")
    public List<Order> getAllOrders() {
        return service.getOrders();
    }
}

