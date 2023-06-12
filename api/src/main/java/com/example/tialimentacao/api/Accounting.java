package com.example.tialimentacao.api;

import com.example.tialimentacao.dto.order.OrderRequestAccountingDTO;
import com.example.tialimentacao.models.Order;
import com.example.tialimentacao.utils.PaymentData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class Accounting {
    private RestTemplate restTemplate;
    private final String url = "http://localhost:8081/accounting";

    public Accounting() {
        this.restTemplate = new RestTemplate();
    }


    public void sendOrder(Order order) {
        RestTemplate restTemplate = new RestTemplate();

        String paymentData = "";

        if(order.getPaymentMethod().equals("PIX")) {
            paymentData = PaymentData.PIX_KEY;
        } else if (order.getPaymentMethod().equals("BOLETO")) {
            paymentData = PaymentData.BAR_CODE;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(
                new OrderRequestAccountingDTO(
                        order.getId(),
                        order.getPaymentMethod(),
                        order.getTotal(),
                        paymentData
                ),
                headers
        );

        restTemplate.postForObject(url, entity, OrderRequestAccountingDTO.class);
    }
}