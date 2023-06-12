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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/orders")
public class OrdersController {
    final OrdersRepository ordersRepository;
    final ProductsOrdersRepository productsOrdersRepository;
    final ProductsRepository productsRepository;

    Accounting accounting = new Accounting();

    public OrdersController(OrdersRepository ordersRepository, ProductsOrdersRepository productsOrdersRepository, ProductsRepository productsRepository) {
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
                        productOrder.getQuantity()
                ));
            });

            ordersResponse.add(new OrderResponseDTO(
                    order.getCompany(),
                    order.getPaymentMethod(),
                    order.getTotal(),
                    order.getStatus(),
                    products
            ));
        });

        return ResponseEntity.ok(ordersResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> show(@PathVariable("id") UUID id) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if(!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = orderOptional.get();

        List<ProductOrder> productsOrders = productsOrdersRepository.findAllByOrderId(order.getId());

        List<ProductOrderResponseDTO> products = new ArrayList<>();

        productsOrders.forEach(productOrder -> {
            products.add(new ProductOrderResponseDTO(
                    productOrder.getProduct().getName(),
                    productOrder.getQuantity()
            ));
        });

        return ResponseEntity.ok(new OrderResponseDTO(
                order.getCompany(),
                order.getPaymentMethod(),
                order.getTotal(),
                order.getStatus(),
                products
        ));
    }

    @PostMapping
    public ResponseEntity<OrderRequestDTO> create(@RequestBody @Valid OrderRequestDTO order) {
        Order orderCreated = ordersRepository.save(new Order(
                order.company(),
                order.paymentMethod(),
                order.total()
        ));

        order.products().forEach(product -> {
            Product productSaved = productsRepository.findByName(product.name());

            productSaved.setQuantity(
                    productSaved.getQuantity() - product.quantity()
            );
            productsRepository.save(productSaved);
            productsOrdersRepository.save(new ProductOrder(product.quantity(), productSaved, orderCreated));
        });


        accounting.sendOrder(orderCreated);

        orderCreated.setStatus(OrderStatus.AWAITING_PAYMENT);

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID id) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if(!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = orderOptional.get();

        List<ProductOrder> productsOrders = productsOrdersRepository.findAllByOrderId(order.getId());

        productsOrders.forEach(productOrder -> {
            productsOrdersRepository.delete(productOrder);

            Product product = productOrder.getProduct();

            product.setQuantity(
                    product.getQuantity() + productOrder.getQuantity()
            );
        });

        ordersRepository.delete(order);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable("id") UUID id, @RequestBody @Valid OrderStatus status) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if(!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        if (!OrderStatus.isValid(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status is invalid");
        }

        Order order = orderOptional.get();

        order.setStatus(status);

        return ResponseEntity.ok("Status updated successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> paymentVoucher(@PathVariable("id") UUID id, @RequestBody @Valid String voucher) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if(!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }


        Order order = orderOptional.get();
        if(voucher.equals("")) {
            order.setStatus(OrderStatus.ORDER_CANCELED);
            return ResponseEntity.ok("Voucher is not valid, order canceled");
        } else {
            order.setStatus(OrderStatus.ORDER_PREPARING);
            return ResponseEntity.ok("Voucher is valid, payment accepted");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> cancelOrder(@PathVariable("id") UUID id) {
        Optional<Order> orderOptional = ordersRepository.findById(id);

        if(!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = orderOptional.get();

        order.setStatus(OrderStatus.ORDER_CANCELED);

        return ResponseEntity.ok("Order canceled successfully");
    }
}