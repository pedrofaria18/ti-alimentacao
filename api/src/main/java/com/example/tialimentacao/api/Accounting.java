package com.example.tialimentacao.api;

import com.example.tialimentacao.dto.order.OrderRequestAccountingDTO;
import com.example.tialimentacao.dto.order.OrderResponseAccountingDTO;
import com.example.tialimentacao.models.Order;

import java.net.http.HttpResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class Accounting {
    private RestTemplate restTemplate;
    private final String url = "http://localhost:8000/api/foodPayment";

    public Accounting() {
        this.restTemplate = new RestTemplate();
    }


    public void sendOrder(Order order) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(
                new OrderRequestAccountingDTO(
                        order.getId(),
                        order.getTotal()
                ),
                headers
        );

        restTemplate.postForObject(url, entity, OrderResponseAccountingDTO.class);
    }
}