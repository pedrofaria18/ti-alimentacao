package com.example.tialimentacao.dto.product;

import com.example.tialimentacao.models.Product;

import java.util.UUID;

public record ProductResponseDTO(UUID id, String name, Double price, Integer quantity) {
    public ProductResponseDTO(Product product) {
        this(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }
}
