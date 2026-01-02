package com.project.paymentservice.model.dto;

import lombok.Data;

@Data
public class ExecuteVirementResponseDTO {
    private ServiceStatusDTO serviceStatus;
    private VirementSoapInfoDTO virementInfo;

    // Getters/Setters
}
