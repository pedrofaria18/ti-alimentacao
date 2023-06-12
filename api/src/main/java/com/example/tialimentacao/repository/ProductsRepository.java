package com.example.tialimentacao.repository;

import com.example.tialimentacao.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductsRepository extends JpaRepository<Product, UUID> {
    boolean existsById(UUID id);
    boolean existsByName(String name);
    Product findByName(String name);
}
