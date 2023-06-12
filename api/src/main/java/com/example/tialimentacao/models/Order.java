package com.example.tialimentacao.models;

import com.example.tialimentacao.utils.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "orders")
@Entity(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private OrderStatus status = OrderStatus.ORDER_RECEIVED;

    @Column(nullable = false)
    private Double total;

    public Order(String company, String paymentMethod, Double total) {
        this.company = company;
        this.paymentMethod = paymentMethod;
        this.total = total;
    }
}