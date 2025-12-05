package com.inventory_service.service;

import com.inventory_service.dto.InventoryResponse;
import com.inventory_service.model.Inventory;
import com.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .inInStock(inventory.getQuantity() > 0)
                                .build()
                )
                .collect(Collectors.toList());
    }

    // Optional: Check stock for single SKU
    public InventoryResponse checkStock(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("Inventory not found for SKU: " + skuCode));

        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .inInStock(inventory.getQuantity() > 0)
                .build();
    }
}