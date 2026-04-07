package com.example.order_service.controller;

import com.example.order_service.dto.OrderSummary;
import com.example.order_service.model.Order;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPlaceOrder() throws Exception {

        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(2);

        OrderSummary summary = new OrderSummary();
        summary.setTotalItems(2);
        summary.setTotalAmount(2000.0);

        when(service.placeOrder(org.mockito.ArgumentMatchers.any(Order.class)))
                .thenReturn(summary);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalAmount").value(2000.0));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {

        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(2);

        when(service.getOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders/getOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}