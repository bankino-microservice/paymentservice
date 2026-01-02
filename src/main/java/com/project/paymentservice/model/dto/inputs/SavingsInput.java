package com.project.paymentservice.model.dto.inputs;

import lombok.Data;

@Data
public class SavingsInput {
    private String Epargnerib;
    private String Courantrib;
    private Double montant;
    private Integer year;
    private Integer month;
    private Integer day;
}