package com.project.paymentservice.model.dto;

import lombok.Data;

@Data
public class CEpargneResponseDTO {
    private String Epargnerib;
    private String Courantrib;
    private Double balanceQ1;
    private Double balanceQ2;
    private Double interets;
    private String status;
    private String message;
}