package com.project.paymentservice.service;

import com.project.paymentservice.model.dto.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Paymentservice {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    private static final String TOPIC = "payment-events";

    public void sendPaymentEvent(PaymentEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println("PaymentEvent sent: " + event);
    }
}