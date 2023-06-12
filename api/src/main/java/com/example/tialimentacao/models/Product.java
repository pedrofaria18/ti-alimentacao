package com.example.tialimentacao.models;

import com.example.tialimentacao.dto.product.ProductRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "products")
@Entity(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    public Product(ProductRequestDTO data) {
        this.name = data.name();
        this.price = data.price();
        this.quantity = data.quantity();
    }
}