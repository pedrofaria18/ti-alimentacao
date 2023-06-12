package com.example.tialimentacao.dto.order;

import java.util.UUID;

public record OrderRequestAccountingDTO(
        UUID orderId,
        String paymentMethod,
        Double total,
        String paymentData

) {
}