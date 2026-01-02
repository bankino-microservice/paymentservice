package com.project.paymentservice.model.dto;

import lombok.Data;

@Data
public class ClotureQuinzaineRequestDTO {
    private String rib;
    private String quinzaineId; // e.g., "2025-Q1"
}