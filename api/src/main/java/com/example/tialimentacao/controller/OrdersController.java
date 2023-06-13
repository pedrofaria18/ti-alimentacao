package com.example.tialimentacao.controller;

import com.example.tialimentacao.api.Accounting;
import com.example.tialimentacao.dto.order.OrderRequestDTO;
import com.example.tialimentacao.dto.order.OrderResponseDTO;
import com.example.tialimentacao.dto.order.ProductOrderResponseDTO;
import com.example.tialimentacao.models.Order;
import com.example.tialimentacao.models.Product;
import com.example.tialimentacao.models.ProductOrder;
import com.example.tialimentacao.repository.OrdersRepository;
import com.example.tialimentacao.repository.ProductsOrdersRepository;
import com.example.tialimentacao.repository.ProductsRepository;
import com.example.tialimentacao.utils.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/orders")
public class OrdersController {
    final OrdersRepository ordersRepository;
    final ProductsOrdersRepository productsOrdersRepository;
    final ProductsRepository productsRepository;

    Accounting accounting = new Accounting();

    public OrdersController(OrdersRepository ordersRepository, ProductsOrdersRepository productsOrdersRepository,
            ProductsRepository productsRepository) {
        this.ordersRepository = ordersRepository;
        this.productsOrdersRepository = productsOrdersRepository;
        this.productsRepository = productsRepository;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> index() {
        List<Order> orders = ordersRepository.findAll();

        List<OrderResponseDTO> ordersResponse = new ArrayList<>();

        orders.forEach(order -> {
            List<ProductOrder> productsOrders = productsOrdersRepository.findAllByOrderId(order.getId());

            List<ProductOrderResponseDTO> products = new ArrayList<>();

            productsOrders.forEach(productOrder -> {
                products.add(new ProductOrderResponseDTO(
                        productOrder.getProduct().getName(),
                        productOrder.getQuantity()));
            });

            ordersResponse.add(new OrderResponseDTO(
                    order.getId(),
                    order.getCompany(),
                    order.getPaymentMethod(),
                    order.getTotal(),
                    order.getStatus(),
                    products));
        });

        return ResponseEntity.ok(ordersResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> show(@PathVariable("id") UUID id) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = orderOptional.get();

        List<ProductOrder> productsOrders = productsOrdersRepository.findAllByOrderId(order.getId());

        List<ProductOrderResponseDTO> products = new ArrayList<>();

        productsOrders.forEach(productOrder -> {
            products.add(new ProductOrderResponseDTO(
                    productOrder.getProduct().getName(),
                    productOrder.getQuantity()));
        });

        return ResponseEntity.ok(new OrderResponseDTO(
                order.getId(),
                order.getCompany(),
                order.getPaymentMethod(),
                order.getTotal(),
                order.getStatus(),
                products));
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid OrderRequestDTO order) {

        if (order.products() == null || order.products().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Products is required");
        }

        Double total = 0.0;

        for (ProductOrderResponseDTO product : order.products()) {
            Product productSaved = productsRepository.findByName(product.name());

            if (productSaved == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            if (product.quantity() > productSaved.getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product quantity is invalid");
            }

            total += productSaved.getPrice() * product.quantity();
        }

        if (!total.equals(order.total())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Total is invalid");
        }

        Order orderCreated = ordersRepository.save(new Order(
                order.company(),
                order.paymentMethod(),
                order.total()));

        for (ProductOrderResponseDTO product : order.products()) {
            Product productSaved = productsRepository.findByName(product.name());

            productsOrdersRepository.save(new ProductOrder(
                    product.quantity(),
                    productSaved,
                    orderCreated));

            productSaved.setQuantity(
                    productSaved.getQuantity() - product.quantity());

            productsRepository.save(productSaved);
        }

        accounting.sendOrder(orderCreated);

        return ResponseEntity.ok(orderCreated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID id) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = orderOptional.get();

        List<ProductOrder> productsOrders = productsOrdersRepository.findAllByOrderId(order.getId());

        productsOrders.forEach(productOrder -> {
            productsOrdersRepository.delete(productOrder);

            Product product = productOrder.getProduct();

            product.setQuantity(
                    product.getQuantity() + productOrder.getQuantity());
        });

        ordersRepository.delete(order);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable("id") UUID id, @RequestBody @Valid OrderStatus status) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = orderOptional.get();

        if (order.getStatus().equals(OrderStatus.ORDER_CANCELED)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is canceled");
        }

        if (order.getStatus().equals(OrderStatus.ORDER_SUCCESS)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is already completed");
        }

        if (order.getStatus().equals(OrderStatus.AWAITING_PAYMENT)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Do not update the status of an order that is awaiting payment");
        }

        if (order.getStatus().equals(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status is the same as the current one");
        }

        EnumSet<OrderStatus> orderStatus = EnumSet.of(
                OrderStatus.ORDER_IN_ROUTE,
                OrderStatus.ORDER_DELIVERED,
                OrderStatus.ORDER_SUCCESS,
                OrderStatus.ORDER_CANCELED);

        if (!orderStatus.contains(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status is invalid or not allowed");
        }

        order.setStatus(status);
        ordersRepository.save(order);

        return ResponseEntity.ok("Status updated successfully");
    }

    @PostMapping("/receiptPayment/{id}")
    public ResponseEntity<Object> receiptPayment(@PathVariable("id") UUID id, @RequestBody @Valid String voucher) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = orderOptional.get();

        if (!order.getStatus().equals(OrderStatus.AWAITING_PAYMENT)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is not awaiting payment status");
        }

        if (voucher == null || voucher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Voucher is required");
        }

        if (voucher.length() < 44) {
            order.setStatus(OrderStatus.ORDER_CANCELED);
            ordersRepository.save(order);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Voucher is invalid, order canceled");
        }

        order.setStatus(OrderStatus.ORDER_IN_ROUTE);
        ordersRepository.save(order);
        return ResponseEntity.ok("Payment receipt successfully");
    }
}