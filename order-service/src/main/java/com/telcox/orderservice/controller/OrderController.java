package com.telcox.orderservice.controller;

import com.telcox.orderservice.entity.Order;
import com.telcox.orderservice.entity.OrderItem;
import com.telcox.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/{orderId}/items")
    public OrderItem addOrderItem(@PathVariable Long orderId, @RequestBody OrderItem orderItem) {
        return orderService.addOrderItem(orderId, orderItem);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @PostMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @PostMapping("/{id}/confirm")
    public Order confirmOrder(@PathVariable Long id) {
        return orderService.confirmOrder(id);
    }
}
