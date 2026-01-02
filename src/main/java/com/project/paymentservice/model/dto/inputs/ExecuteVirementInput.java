package com.project.paymentservice.model.dto.inputs;

import com.project.paymentservice.model.enums.Type;
import lombok.Data;

@Data
public class ExecuteVirementInput {
    private String ribEmetteur;
    private String ribRecepteur;
    private Double montant;
    private Type type;
}
