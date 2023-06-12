package com.example.tialimentacao.dto.order;

import com.example.tialimentacao.utils.OrderStatus;

import java.util.List;

public record OrderResponseDTO(String company, String paymentMethod, Double total, OrderStatus status, List<ProductOrderResponseDTO> products) {}