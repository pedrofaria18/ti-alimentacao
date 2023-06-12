package com.example.tialimentacao.dto.product;

import com.example.tialimentacao.models.Product;
import lombok.Getter;

public record ProductRequestDTO(String name, Double price, Integer quantity) {
    public ProductRequestDTO(Product product) {
        this(product.getName(), product.getPrice(), product.getQuantity());
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
