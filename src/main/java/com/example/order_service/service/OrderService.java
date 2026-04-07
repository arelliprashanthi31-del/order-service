package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.Item;
import com.example.order_service.dto.OrderItemSummary;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.OrderSummary;
import com.example.order_service.model.Order;
import com.example.order_service.model.Product;
import com.example.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repo;

    @Autowired
    private ProductClient productClient;

    @Transactional
    public OrderSummary placeOrder(Order order) {

        // Step 1: Prepare request
        OrderRequest request = new OrderRequest();

        Item item = new Item();
        item.setProductId(order.getProductId());
        item.setQuantity(order.getQuantity());

        request.setItems(List.of(item));

        try {
            // Step 2: Reserve stock
            productClient.reserve(request);

            // Step 3: Save order as PENDING
            order.setStatus("PENDING");
            Order savedOrder = repo.save(order);

            // (Optional) simulate payment success
            boolean paymentSuccess = true;

            if (!paymentSuccess) {
                throw new RuntimeException("Payment failed");
            }

            // Step 4: Confirm stock
            productClient.confirm(request);

            savedOrder.setStatus("CONFIRMED");
            repo.save(savedOrder);

            Product product = productClient.getProduct(order.getProductId());

            return generateSummary(List.of(savedOrder), List.of(product));

        } catch (Exception e) {

            // Step 5: Compensation
            try {
                productClient.release(request);
            } catch (Exception ex) {
                System.out.println("Release failed - manual intervention needed");
            }

            order.setStatus("FAILED");
            repo.save(order);
            throw new RuntimeException("Order failed: " + e.getMessage());
        }
    }

    private OrderSummary generateSummary(List<Order> orders, List<Product> products) {

        List<OrderItemSummary> itemSummaries = orders.stream()
                .map(order -> {

                    Product product = products.stream()
                            .filter(p -> p.getId().equals(order.getProductId()))
                            .findFirst()
                            .orElseThrow();

                    OrderItemSummary item = new OrderItemSummary();
                    item.setProductId(order.getProductId());
                    item.setQuantity(order.getQuantity());
                    item.setPrice(product.getPrice());
                    item.setTotalPrice(product.getPrice() * order.getQuantity());

                    return item;
                })
                .toList();

        int totalItems = itemSummaries.stream()
                .mapToInt(OrderItemSummary::getQuantity)
                .sum();

        double totalAmount = itemSummaries.stream()
                .mapToDouble(OrderItemSummary::getTotalPrice)
                .sum();

        OrderSummary summary = new OrderSummary();
        summary.setItems(itemSummaries);
        summary.setTotalItems(totalItems);
        summary.setTotalAmount(totalAmount);

        return summary;
    }
    public  List<Order> getOrders(){
        return repo.findAll();
    }
}