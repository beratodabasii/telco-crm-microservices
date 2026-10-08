package com.telcox.orderservice.service;

import com.telcox.orderservice.client.CustomerClient;
import com.telcox.orderservice.client.CustomerResponse;
import com.telcox.orderservice.client.ProductCatalogClient;
import com.telcox.orderservice.client.TariffResponse;
import com.telcox.orderservice.dto.AddOrderItemRequest;
import com.telcox.orderservice.dto.CreateOrderRequest;
import com.telcox.orderservice.dto.OrderItemResponse;
import com.telcox.orderservice.dto.OrderResponse;
import com.telcox.orderservice.entity.Order;
import com.telcox.orderservice.entity.OrderItem;
import com.telcox.orderservice.entity.OutboxEvent;
import com.telcox.orderservice.enums.OrderStatus;
import com.telcox.orderservice.event.OrderCancelledEvent;
import com.telcox.orderservice.event.OrderConfirmedEvent;
import com.telcox.orderservice.event.OrderCreatedEvent;
import com.telcox.orderservice.event.OrderPaidEvent;
import com.telcox.orderservice.exception.CustomerNotActiveException;
import com.telcox.orderservice.repository.OrderItemRepository;
import com.telcox.orderservice.repository.OrderRepository;
import com.telcox.orderservice.repository.OutboxEventRepository;
import com.telcox.orderservice.repository.SagaStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SagaStateRepository sagaStateRepository;
    private final ProductCatalogClient productCatalogClient;
    private final CustomerClient customerClient;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request){
        CustomerResponse customerResponse = customerClient.getCustomer(request.getCustomerId());
        if (!customerResponse.getStatus().equals("ACTIVE")) {
            throw new CustomerNotActiveException("Customer is not active");
        }
        Order newOrder = new Order();
        newOrder.setCustomerId(request.getCustomerId());
        newOrder.setStatus(OrderStatus.DRAFT);
        newOrder.setTotalAmount(BigDecimal.ZERO);
        newOrder.setCurrency(request.getCurrency());
        newOrder.setCreatedAt(LocalDateTime.now());
        orderRepository.save(newOrder);
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderId(newOrder.getId());
        orderCreatedEvent.setCustomerId(customerResponse.getId());
        orderCreatedEvent.setTotalAmount(newOrder.getTotalAmount());
        orderCreatedEvent.setCurrency(newOrder.getCurrency());
        orderCreatedEvent.setCreatedAt(newOrder.getCreatedAt());
        String payload = objectMapper.writeValueAsString(orderCreatedEvent);
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("OrderCreatedEvent");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);
        outboxEventRepository.save(outboxEvent);
        return mapToResponse(newOrder);

    }

    public OrderResponse getOrderById(Long id){
      Order order =  orderRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Order not found")
        );

        return mapToResponse(order);

    }

    public List<OrderItemResponse> getOrderItems(Long orderId){
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
                .map(this::mapItemToResponse)
                .toList();

    }

    @Transactional
    public OrderItemResponse addOrderItem(Long orderId, AddOrderItemRequest request) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );

        TariffResponse tariffResponse =
                productCatalogClient.getTariff(request.getProductCode());

        BigDecimal itemTotal = tariffResponse.getMonthlyFee()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        BigDecimal newTotal = order.getTotalAmount().add(itemTotal);
        order.setTotalAmount(newTotal);

        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setOrderId(orderId);
        newOrderItem.setProductCode(request.getProductCode());
        newOrderItem.setProductType(request.getProductType());
        newOrderItem.setQuantity(request.getQuantity());
        newOrderItem.setUnitPrice(tariffResponse.getMonthlyFee());

        OrderItem savedOrderItem = orderItemRepository.save(newOrderItem);
        orderRepository.save(order);

        return mapItemToResponse(savedOrderItem);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent();
        orderCancelledEvent.setOrderId(orderId);
        orderCancelledEvent.setCustomerId(order.getCustomerId());
        orderCancelledEvent.setCancelledAt(LocalDateTime.now());

        String payload = objectMapper.writeValueAsString(orderCancelledEvent);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("OrderCancelledEvent");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);

        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        } else if (order.getStatus() != OrderStatus.DRAFT) {
            throw new RuntimeException("Order is not in DRAFT status");
        }

        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Order total must be greater than zero");
        }

        order.setStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);

        OrderConfirmedEvent orderConfirmedEvent = new OrderConfirmedEvent();
        orderConfirmedEvent.setOrderId(order.getId());
        orderConfirmedEvent.setCustomerId(order.getCustomerId());
        orderConfirmedEvent.setTotalAmount(order.getTotalAmount());
        orderConfirmedEvent.setCurrency(order.getCurrency());
        orderConfirmedEvent.setConfirmedAt(LocalDateTime.now());

        String payload = objectMapper.writeValueAsString(orderConfirmedEvent);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("OrderConfirmedEvent");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);

        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse markOrderAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );

        OrderItem orderItem = orderItemRepository.findFirstByOrderId(orderId).orElseThrow(
                () -> new RuntimeException("OrderItem not found")
        );

        if (order.getStatus() == OrderStatus.PAID ||
                order.getStatus() == OrderStatus.FULFILLED) {
            return mapToResponse(order);
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Order is not in PENDING_PAYMENT status");
        }

        OrderPaidEvent orderPaidEvent = new OrderPaidEvent();
        orderPaidEvent.setOrderId(orderId);
        orderPaidEvent.setCustomerId(order.getCustomerId());
        orderPaidEvent.setTariffCode(orderItem.getProductCode());
        orderPaidEvent.setPaidAt(LocalDateTime.now());

        String payload = objectMapper.writeValueAsString(orderPaidEvent);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("OrderPaidEvent");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return mapToResponse(order);
    }

    public OrderResponse markOrderAsFulfilled(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );

        if (order.getStatus() == OrderStatus.FULFILLED) {
            return mapToResponse(order);
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Order must be PAID before fulfillment");
        }

        order.setStatus(OrderStatus.FULFILLED);
        orderRepository.save(order);

        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCurrency(order.getCurrency());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setOrderId(item.getOrderId());
        response.setProductCode(item.getProductCode());
        response.setProductType(item.getProductType());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        return response;
    }
}
