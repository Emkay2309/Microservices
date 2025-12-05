package com.inventory_service.repository;

import com.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Correct: Accepts List<String> instead of String
    List<Inventory> findBySkuCodeIn(List<String> skuCodes);

    // Optional: Also add method to find by single SKU code
    Optional<Inventory> findBySkuCode(String skuCode);
}