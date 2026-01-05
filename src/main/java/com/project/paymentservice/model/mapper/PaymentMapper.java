package com.project.paymentservice.model.mapper;

import com.project.paymentservice.model.dto.*;
import com.project.paymentservice.model.dto.inputs.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // Virement Mapping
    ExecuteVirementRequestDTO toVirementRequest(ExecuteVirementInput input);

    // Savings Mapping
    // Since fields (rib, amount) match exactly, MapStruct handles it automatically
    DepotRetraitCEpargneRequestDTO toSavingsRequest(DepotRetraitCEpargneRequestDTO input);

    // Closing Operations Mapping
    ClotureQuinzaineRequestDTO toQuinzaineRequest(QuinzaineInput input);

    CalculateAnnualeRequestDTO toAnnualRequest(AnnualInput input);
}