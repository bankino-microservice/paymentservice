package com.project.paymentservice.testutil;

import com.project.paymentservice.model.dto.*;
import com.project.paymentservice.model.dto.event.PaymentEvent;
import com.project.paymentservice.model.dto.inputs.*;
import com.project.paymentservice.model.enums.Type;

import java.math.BigDecimal;

/**
 * Test data builder utility class for creating test DTOs, inputs, and events.
 * Provides factory methods with sensible defaults to reduce test boilerplate.
 */
public class TestDataBuilder {

    // ========== Input DTOs ==========

    public static ExecuteVirementInput createExecuteVirementInput() {
        ExecuteVirementInput input = new ExecuteVirementInput();
        input.setRibEmetteur("RIB123456789");
        input.setRibRecepteur("RIB987654321");
        input.setMontant(1000.0);
        input.setType(Type.INSTANTANEE);
        return input;
    }

    public static ExecuteVirementInput createExecuteVirementInput(String ribEmetteur, String ribRecepteur,
            Double montant, Type type) {
        ExecuteVirementInput input = new ExecuteVirementInput();
        input.setRibEmetteur(ribEmetteur);
        input.setRibRecepteur(ribRecepteur);
        input.setMontant(montant);
        input.setType(type);
        return input;
    }

    public static DepotRetraitCEpargneRequestDTO createSavingsInput() {
        DepotRetraitCEpargneRequestDTO input = new DepotRetraitCEpargneRequestDTO();
        input.setEpargnerib("EPARGNE123");
        input.setCourantrib("COURANT456");
        input.setMontant(500.0);
        return input;
    }

    public static DepotRetraitCEpargneRequestDTO createSavingsInput(String epargneRib, String courantRib, Double montant) {
        DepotRetraitCEpargneRequestDTO input = new DepotRetraitCEpargneRequestDTO();
        input.setEpargnerib(epargneRib);
        input.setCourantrib(courantRib);
        input.setMontant(montant);
        return input;
    }

    public static QuinzaineInput createQuinzaineInput() {
        QuinzaineInput input = new QuinzaineInput();
        input.setRib("RIB123456789");
        input.setYear(2025);
        input.setQuaiznine(24);
        return input;
    }

    public static QuinzaineInput createQuinzaineInput(String rib, Integer year, Integer quaiznine) {
        QuinzaineInput input = new QuinzaineInput();
        input.setRib(rib);
        input.setYear(year);
        input.setQuaiznine(quaiznine);
        return input;
    }

    public static AnnualInput createAnnualInput() {
        AnnualInput input = new AnnualInput();
        input.setRib("RIB123456789");
        input.setYear(2025);
        return input;
    }

    public static AnnualInput createAnnualInput(String rib, int year) {
        AnnualInput input = new AnnualInput();
        input.setRib(rib);
        input.setYear(year);
        return input;
    }

    // ========== Request DTOs ==========

    public static ExecuteVirementRequestDTO createExecuteVirementRequestDTO() {
        ExecuteVirementRequestDTO dto = new ExecuteVirementRequestDTO();
        dto.setRibEmetteur("RIB123456789");
        dto.setRibRecepteur("RIB987654321");
        dto.setMontant(1000.0);
        dto.setType(Type.INSTANTANEE);
        return dto;
    }

    public static DepotRetraitCEpargneRequestDTO createDepotRetraitCEpargneRequestDTO() {
        DepotRetraitCEpargneRequestDTO dto = new DepotRetraitCEpargneRequestDTO();
        dto.setEpargnerib("EPARGNE123");
        dto.setCourantrib("COURANT456");
        dto.setMontant(500.0);

        return dto;
    }

    public static ClotureQuinzaineRequestDTO createClotureQuinzaineRequestDTO() {
        ClotureQuinzaineRequestDTO dto = new ClotureQuinzaineRequestDTO();
        dto.setRib("RIB123456789");
        dto.setQuinzaineId("2025-Q24");
        return dto;
    }

    public static CalculateAnnualeRequestDTO createCalculateAnnualeRequestDTO() {
        CalculateAnnualeRequestDTO dto = new CalculateAnnualeRequestDTO();
        dto.setRib("RIB123456789");
        dto.setYear(2025);
        return dto;
    }

    public static GetClientIdByRibRequestDTO createGetClientIdByRibRequestDTO(String rib) {
        GetClientIdByRibRequestDTO dto = new GetClientIdByRibRequestDTO();
        dto.setRib(rib);
        return dto;
    }

    // ========== Response DTOs ==========

    public static ExecuteVirementResponseDTO createExecuteVirementResponseDTO() {
        ExecuteVirementResponseDTO dto = new ExecuteVirementResponseDTO();
        ServiceStatusDTO serviceStatus = new ServiceStatusDTO();
        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Operation successful");
        dto.setServiceStatus(serviceStatus);
        VirementSoapInfoDTO virementInfo = new VirementSoapInfoDTO();
        virementInfo.setId(1L);
        virementInfo.setCompteEmetteurRib("RIB123456789");
        virementInfo.setCompteRecepteurRib("RIB987654321");
        virementInfo.setMontant(1000.0);
        virementInfo.setType("INSTANTANEE");
        dto.setVirementInfo(virementInfo);
        return dto;
    }

    public static CEpargneResponseDTO createCEpargneResponseDTO() {
        CEpargneResponseDTO dto = new CEpargneResponseDTO();
        dto.setStatus("SUCCESS");
        dto.setMessage("Operation completed successfully");
        return dto;
    }

    public static ClotureAnnuelleResponseDTO createClotureQuinzaineResponseDTO() {
        ClotureAnnuelleResponseDTO dto = new ClotureAnnuelleResponseDTO();
        dto.setStatus("SUCCESS");
        dto.setMessage("Quinzaine closed successfully");
        return dto;
    }

    public static ClotureAnnuelleResponseDTO createClotureAnnuelleResponseDTO() {
        ClotureAnnuelleResponseDTO dto = new ClotureAnnuelleResponseDTO();
        dto.setStatus("SUCCESS");
        dto.setMessage("Annual closure completed");
        return dto;
    }

    public static GetClientIdByRibResponseDTO createGetClientIdByRibResponseDTO(Long clientId) {
        GetClientIdByRibResponseDTO dto = new GetClientIdByRibResponseDTO();
        dto.setClientId(clientId);
        return dto;
    }

    public static RegisterClientResDTO createRegisterClientResDTO() {
        return RegisterClientResDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .job("Engineer")
                .username("john.doe")
                .build();
    }

    public static RegisterClientResDTO createRegisterClientResDTO(Long id, String firstName, String lastName,
            String email, String phone) {
        return RegisterClientResDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .job("Engineer")
                .username(email != null ? email.split("@")[0] : "user")
                .build();
    }

    // ========== Events ==========

    public static PaymentEvent createPaymentEvent() {
        return PaymentEvent.builder()
                .clientEmail("john.doe@example.com")
                .clientName("John Doe")
                .amount(BigDecimal.valueOf(1000.00))
                .clientPhoneNumber("1234567890")
                .description("Test payment event")
                .build();
    }

    public static PaymentEvent createPaymentEvent(String email, String name, BigDecimal amount, String phone,
            String description) {
        return PaymentEvent.builder()
                .clientEmail(email)
                .clientName(name)
                .amount(amount)
                .clientPhoneNumber(phone)
                .description(description)
                .build();
    }

    // ========== Helper Methods ==========

    /**
     * Creates a null payment event for testing null handling
     */
    public static PaymentEvent createNullPaymentEvent() {
        return null;
    }

    /**
     * Creates an event with null fields for edge case testing
     */
    public static PaymentEvent createPaymentEventWithNullFields() {
        return PaymentEvent.builder()
                .clientEmail(null)
                .clientName(null)
                .amount(null)
                .clientPhoneNumber(null)
                .description(null)
                .build();
    }
}
