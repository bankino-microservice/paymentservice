package com.project.paymentservice.model.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent  {

    private Long paymentId;
    private String clientEmail;
    private String clientName;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;

    private String transactionId;
    private String description;

}