package com.project.paymentservice.model.dto;

import lombok.Data;

@Data
public class VirementSoapInfoDTO {
    private Long id;
    private String type;
    private double montant;
    private String date;
    private String compteEmetteurRib;
    private String compteRecepteurRib;
    private double ammouttakefromyou;
}