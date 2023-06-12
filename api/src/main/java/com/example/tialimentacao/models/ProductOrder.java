package com.example.tialimentacao.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "product_orders")
@Entity(name = "product_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer quantity;

    public ProductOrder(Integer quantity, Product product, Order order) {
        this.quantity = quantity;
        this.product = product;
        this.order= order;
    }
}
