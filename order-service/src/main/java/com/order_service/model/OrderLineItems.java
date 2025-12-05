package com.order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_line_items") // Fixed table name (singular to plural)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skuCode;
    private Double price;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id") // Added for bidirectional relationship
    private Order order;
}