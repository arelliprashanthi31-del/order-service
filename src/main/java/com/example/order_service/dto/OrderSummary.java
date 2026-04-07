package com.example.order_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderSummary {

    private int totalItems;
    private double totalAmount;
    private List<OrderItemSummary> items;
}
