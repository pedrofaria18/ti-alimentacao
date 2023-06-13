package com.example.tialimentacao.dto.order;

import java.util.UUID;

public record OrderRequestAccountingDTO(
        UUID purchase_id,
        Double value
) {
}