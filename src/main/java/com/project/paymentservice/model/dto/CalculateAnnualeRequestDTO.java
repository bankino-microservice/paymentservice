package com.project.paymentservice.model.dto;

import lombok.Data;

@Data
public class CalculateAnnualeRequestDTO {
    private String rib;
    private int year;
}