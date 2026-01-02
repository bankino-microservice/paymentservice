package com.project.paymentservice.model.dto;

import com.project.paymentservice.model.enums.Type;
import lombok.Data;

@Data
public class ExecuteVirementRequestDTO {
    private String ribEmetteur;
    private String ribRecepteur;
    private Double montant;
    private Type type;
}
