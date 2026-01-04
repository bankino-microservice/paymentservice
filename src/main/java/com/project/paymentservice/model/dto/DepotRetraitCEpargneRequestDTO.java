package com.project.paymentservice.model.dto;

import lombok.Data;

@Data
public class DepotRetraitCEpargneRequestDTO {
    private String Epargnerib;
    private String Courantrib;
    private Double montant;

}