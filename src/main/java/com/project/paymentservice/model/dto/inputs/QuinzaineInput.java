package com.project.paymentservice.model.dto.inputs;

import lombok.Data;

@Data
public class QuinzaineInput {
    private String rib;
    private Integer year;
    private Integer quaiznine; // Matches XSD naming
}