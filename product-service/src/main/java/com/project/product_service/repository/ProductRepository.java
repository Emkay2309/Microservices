package com.project.product_service.repository;


import com.project.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by exact name
    Optional<Product> findByName(String name);


    // Find by category
    List<Product> findByCategory(String category);


}


