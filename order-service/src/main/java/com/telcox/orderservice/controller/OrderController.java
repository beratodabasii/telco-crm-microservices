package com.telcox.orderservice.controller;

import com.telcox.orderservice.dto.AddOrderItemRequest;
import com.telcox.orderservice.dto.CreateOrderRequest;
import com.telcox.orderservice.dto.OrderItemResponse;
import com.telcox.orderservice.dto.OrderResponse;
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
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/{orderId}/items")
    public OrderItemResponse addOrderItem(
            @PathVariable Long orderId,
            @RequestBody AddOrderItemRequest request) {

        return orderService.addOrderItem(orderId, request);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItemResponse> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @PostMapping("/{id}/confirm")
    public OrderResponse confirmOrder(@PathVariable Long id) {
        return orderService.confirmOrder(id);
    }
}
