package com.example.tialimentacao.repository;

import com.example.tialimentacao.models.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductsOrdersRepository extends JpaRepository<ProductOrder, UUID>
{
    List<ProductOrder> findAllByOrderId(UUID id);
}