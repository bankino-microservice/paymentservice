package com.project.paymentservice.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirementDTO {
    private Long id;
    private String compteEmetteurRib;
    private String compteRecepteurRib;
    private BigDecimal montant;
    private String description;
    private String type;

    @JsonProperty("dateExecution")
    private LocalDateTime dateExecution;

    @JsonProperty("date")
    private String date;

    private String statut;

    @JsonProperty("ammouttakefromyou")
    private Double ammouttakefromyou;
}
