package com.order_service.repository;

import com.order_service.model.OrderLineItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineItemsRepository extends JpaRepository<OrderLineItems, Long> {
    List<OrderLineItems> findBySkuCode(String skuCode);
    List<OrderLineItems> findByOrderId(Long orderId);
}