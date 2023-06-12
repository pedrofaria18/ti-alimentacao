package com.example.tialimentacao.dto.order;

import com.example.tialimentacao.utils.OrderStatus;

import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(UUID id, String company, String paymentMethod, Double total, OrderStatus status, List<ProductOrderResponseDTO> products) {}