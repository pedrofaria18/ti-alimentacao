package com.example.tialimentacao.controller;

import com.example.tialimentacao.dto.product.ProductRequestDTO;
import com.example.tialimentacao.dto.product.ProductResponseDTO;
import com.example.tialimentacao.models.Product;
import com.example.tialimentacao.repository.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/products")
public class ProductsController {
    final ProductsRepository productsRepository;

    public ProductsController(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @GetMapping
    public ResponseEntity<List<Product>> index() {
        List<Product> products = productsRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> show(@PathVariable("id") UUID id) {
        Optional<Product> productOptional = productsRepository.findById(id);

        if(!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ProductResponseDTO(productOptional.get()));
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ProductRequestDTO data) {
        if(productsRepository.existsByName(data.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already exists");
        }

        Product product = new Product(data);
        System.out.println(product.getId());
        productsRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductResponseDTO(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") UUID id, @RequestBody @Valid ProductRequestDTO data) {
        Optional<Product> productOptional = productsRepository.findById(id);

        if(!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        if(productsRepository.existsByName(data.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already exists");
        }

        Product product = productsRepository.findById(id).get();
        product.setName(data.getName());
        product.setPrice(data.getPrice());
        productsRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body(new ProductResponseDTO(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID id) {
        Optional<Product> productOptional = productsRepository.findById(id);

        if(!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        productsRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted");
    }
}