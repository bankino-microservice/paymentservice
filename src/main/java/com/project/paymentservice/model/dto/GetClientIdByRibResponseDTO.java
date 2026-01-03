package com.project.paymentservice.model.dto;


import com.project.paymentservice.model.dto.response.ServiceStatusDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetClientIdByRibResponseDTO {
    private Long clientId;
    private String rib;
    private com.project.paymentservice.model.dto.response.ServiceStatusDTO serviceStatus;
}
