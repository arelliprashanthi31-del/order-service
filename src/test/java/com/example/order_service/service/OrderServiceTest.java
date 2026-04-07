package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderSummary;
import com.example.order_service.model.Order;
import com.example.order_service.model.Product;
import com.example.order_service.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repo;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService service;

    //  SUCCESS CASE
    @Test
    void shouldPlaceOrderSuccessfully() {

        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(2);

        Product product = new Product();
        product.setId(1L);
        product.setPrice(1000.0);

        when(repo.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(productClient.getProduct(1L)).thenReturn(product);

        OrderSummary result = service.placeOrder(order);

        assertNotNull(result);
        assertEquals(2, result.getTotalItems());
        assertEquals(2000.0, result.getTotalAmount());

        verify(productClient).reserve(any());
        verify(productClient).confirm(any());
        verify(repo, atLeastOnce()).save(any(Order.class));
    }

    //  FAILURE CASE (reserve fails)
    @Test
    void shouldFailAndReleaseStock_whenExceptionOccurs() {

        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(2);

        doThrow(new RuntimeException("Stock error"))
                .when(productClient).reserve(any());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.placeOrder(order));

        assertTrue(exception.getMessage().contains("Order failed"));

        verify(productClient).release(any());
        verify(repo).save(any(Order.class));
    }

    //  GET ORDERS
    @Test
    void shouldReturnAllOrders() {

        Order order = new Order();
        order.setProductId(1L);

        when(repo.findAll()).thenReturn(List.of(order));

        List<Order> result = service.getOrders();

        assertEquals(1, result.size());
        verify(repo).findAll();
    }
}