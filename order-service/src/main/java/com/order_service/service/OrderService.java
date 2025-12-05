package com.order_service.service;

import com.order_service.dto.OrderLineItemsDto;
import com.order_service.dto.OrderRequest;
import com.order_service.dto.OrderResponse;
import com.order_service.model.Order;
import com.order_service.model.OrderLineItems;
import com.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        // Set the relationship
        orderLineItems.forEach(item -> item.setOrder(order));
        order.setOrderLineItemsList(orderLineItems);

        //call Inventory service and place order if product is in stock
        Boolean result = webClient.get()
                .uri("http://localhost:9094/api/inventory")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        Order savedOrder = null;

        if (Boolean.TRUE.equals(result)) {
            savedOrder = orderRepository.save(order);
        }
        else {
            throw  new IllegalArgumentException("Order Not Found");
        }

        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToOrderResponse(order);
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));
        return mapToOrderResponse(order);
    }

    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Clear existing items and add new ones
        existingOrder.getOrderLineItemsList().clear();

        List<OrderLineItems> newItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        newItems.forEach(item -> item.setOrder(existingOrder));
        existingOrder.setOrderLineItemsList(newItems);

        Order updatedOrder = orderRepository.save(existingOrder);
        return mapToOrderResponse(updatedOrder);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private OrderLineItems mapToEntity(OrderLineItemsDto dto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(dto.getSkuCode());
        orderLineItems.setPrice(dto.getPrice());
        orderLineItems.setQuantity(dto.getQuantity());
        return orderLineItems;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());

        List<OrderLineItemsDto> itemsDto = order.getOrderLineItemsList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        response.setOrderLineItemsDtoList(itemsDto);
        response.setTotalAmount(calculateTotalAmount(order));
        return response;
    }

    private OrderLineItemsDto mapToDto(OrderLineItems item) {
        OrderLineItemsDto dto = new OrderLineItemsDto();
        dto.setId(item.getId());
        dto.setSkuCode(item.getSkuCode());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    private Double calculateTotalAmount(Order order) {
        return order.getOrderLineItemsList()
                .stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}