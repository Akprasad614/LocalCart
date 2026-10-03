package com.localcart.controller;

import com.localcart.dto.CreateOrderRequest;
import com.localcart.entity.Order;
import com.localcart.entity.OrderStatus;
import com.localcart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        Optional<Order> order =
                orderService.createOrder(request);

        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id) {

        Optional<Order> order =
                orderService.getOrderById(id);

        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Order>> getOrdersByShop(
            @PathVariable Long shopId) {

        Optional<List<Order>> orders =
                orderService.getOrdersByShop(shopId);

        if (orders.isPresent()) {
            return ResponseEntity.ok(orders.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(
            @PathVariable Long customerId) {

        Optional<List<Order>> orders =
                orderService.getOrdersByCustomer(customerId);

        if (orders.isPresent()) {
            return ResponseEntity.ok(orders.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        Optional<Order> order =
                orderService.updateOrderStatus(id, status);

        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long id) {

        Optional<Order> order =
                orderService.cancelOrder(id);

        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        }

        return ResponseEntity.badRequest().build();
    }
}