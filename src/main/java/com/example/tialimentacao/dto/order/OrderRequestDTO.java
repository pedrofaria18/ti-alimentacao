package com.example.tialimentacao.dto.order;

import java.util.List;

public record OrderRequestDTO(String company, String paymentMethod, Double total, List<ProductOrderResponseDTO> products) {}
