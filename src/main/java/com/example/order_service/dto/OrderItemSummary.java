package com.example.order_service.dto;

import lombok.Data;

@Data
public class OrderItemSummary {

    private Long productId;
    private int quantity;
    private double price;
    private double totalPrice;
}
