package com.project.paymentservice.model.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentEventMock {

    public static final PaymentEvent STATIC_PAYMENT_EVENT =
            PaymentEvent.builder()
                    .paymentId(1L)
                    .clientEmail("bilallaariny01@gmail.com")
                    .clientName("John Doe")
                    .amount(new BigDecimal("500.00"))
                    .currency("EUR")
                    .paymentMethod("VIREMENT")
                    .status("SUCCESS")
                    .transactionId("TXN-STATIC-001")

                    .description("Virement bancaire statique pour test")
                    .build();
}