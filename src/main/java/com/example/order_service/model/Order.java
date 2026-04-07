package com.example.order_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")   // avoid keyword issue
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private int quantity;
    private String status;

}
