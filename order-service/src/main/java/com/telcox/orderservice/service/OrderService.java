package com.telcox.orderservice.service;

import com.telcox.orderservice.client.CustomerClient;
import com.telcox.orderservice.client.CustomerResponse;
import com.telcox.orderservice.client.ProductCatalogClient;
import com.telcox.orderservice.client.TariffResponse;
import com.telcox.orderservice.entity.Order;
import com.telcox.orderservice.entity.OrderItem;
import com.telcox.orderservice.entity.OutboxEvent;
import com.telcox.orderservice.enums.OrderStatus;
import com.telcox.orderservice.event.OrderCancelledEvent;
import com.telcox.orderservice.event.OrderConfirmedEvent;
import com.telcox.orderservice.event.OrderCreatedEvent;
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
    public Order createOrder(Order order){
        CustomerResponse customerResponse = customerClient.getCustomer(order.getCustomerId());
        if (!customerResponse.getStatus().equals("ACTIVE")) {
            throw new CustomerNotActiveException("Customer is not active");
        }
        Order newOrder = new Order();
        newOrder.setCustomerId(customerResponse.getId());
        newOrder.setStatus(OrderStatus.DRAFT);
        newOrder.setTotalAmount(BigDecimal.ZERO);
        newOrder.setCurrency(order.getCurrency());
        newOrder.setCreatedAt(LocalDateTime.now());
        orderRepository.save(newOrder);
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderId(newOrder.getId());
        orderCreatedEvent.setCustomerId(customerResponse.getId());
        orderCreatedEvent.setTotalAmount(newOrder.getTotalAmount());
        orderCreatedEvent.setCurrency(order.getCurrency());
        orderCreatedEvent.setCreatedAt(newOrder.getCreatedAt());
        String payload = objectMapper.writeValueAsString(orderCreatedEvent);
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("OrderCreatedEvent");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);
        outboxEventRepository.save(outboxEvent);
        return newOrder;

    }

    public Order getOrderById(Long id){
        return orderRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Order not found")
        );

    }

    public List<OrderItem> getOrderItems(Long orderId){
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional
    public OrderItem addOrderItem(Long orderId, OrderItem orderItem){
     Order order   =  orderRepository.findById(orderId).orElseThrow(
              ()-> new RuntimeException("Order not found")

      );

     TariffResponse tariffResponse = productCatalogClient.getTariff(orderItem.getProductCode());

     BigDecimal itemTotal = tariffResponse.getMonthlyFee()
             .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

     BigDecimal newTotal = order.getTotalAmount().add(itemTotal);

     order.setTotalAmount(newTotal);

      OrderItem newOrderItem = new OrderItem();
      newOrderItem.setOrderId(orderId);
      newOrderItem.setProductCode(orderItem.getProductCode());
      newOrderItem.setProductType(orderItem.getProductType());
      newOrderItem.setQuantity(orderItem.getQuantity());
      newOrderItem.setUnitPrice(tariffResponse.getMonthlyFee());
      orderItemRepository.save(newOrderItem);
      orderRepository.save(order);
      return newOrderItem ;

    }
    @Transactional
    public Order cancelOrder(Long orderId){
        Order order =  orderRepository.findById(orderId).orElseThrow(
                ()-> new RuntimeException("Order not found")
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
        return order;
    }
    @Transactional
    public Order confirmOrder(Long orderId){
        Order order =  orderRepository.findById(orderId).orElseThrow(
                ()-> new RuntimeException("Order not found")
        );
        if( order.getStatus() == OrderStatus.CANCELLED){
            throw new RuntimeException("Order is already cancelled");
        } else if (order.getStatus() != OrderStatus.DRAFT ) {
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
        return order;

    }

    public Order markOrderAsPaid(Long orderId){
        Order order =  orderRepository.findById(orderId).orElseThrow(
                ()-> new RuntimeException("Order not found")
        );

        if(order.getStatus() != OrderStatus.PENDING_PAYMENT){
            throw new RuntimeException("Order is not in PENDING_PAYMENT status");
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return order;
    }

}
