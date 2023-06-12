package com.example.tialimentacao.dto.order;

import java.util.UUID;

public record ProductOrderResponseDTO(
        String name,
        Integer quantity
) {
}
