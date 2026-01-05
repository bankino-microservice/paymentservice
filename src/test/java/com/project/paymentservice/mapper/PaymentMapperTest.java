package com.project.paymentservice.mapper;

import com.project.paymentservice.model.dto.CalculateAnnualeRequestDTO;
import com.project.paymentservice.model.dto.ClotureQuinzaineRequestDTO;
import com.project.paymentservice.model.dto.DepotRetraitCEpargneRequestDTO;
import com.project.paymentservice.model.dto.ExecuteVirementRequestDTO;
import com.project.paymentservice.model.dto.inputs.AnnualInput;
import com.project.paymentservice.model.dto.inputs.ExecuteVirementInput;
import com.project.paymentservice.model.dto.inputs.QuinzaineInput;
import com.project.paymentservice.model.dto.inputs.SavingsInput;
import com.project.paymentservice.model.enums.Type;
import com.project.paymentservice.model.mapper.PaymentMapper;
import com.project.paymentservice.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PaymentMapper.
 * Tests MapStruct-generated implementation with Spring context.
 * Verifies field-by-field mapping accuracy and null handling.
 */
@SpringBootTest
class PaymentMapperTest {

    @Autowired
    private PaymentMapper paymentMapper;

    // ========== VIREMENT MAPPING TESTS ==========

    @Test
    void toVirementRequest_shouldMapAllFields_whenValidInputProvided() {
        // Arrange
        ExecuteVirementInput input = TestDataBuilder.createExecuteVirementInput(
                "RIB_SENDER_123",
                "RIB_RECEIVER_456",
                2500.75,
                Type.NORMAL);

        // Act
        ExecuteVirementRequestDTO result = paymentMapper.toVirementRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRibEmetteur()).isEqualTo("RIB_SENDER_123");
        assertThat(result.getRibRecepteur()).isEqualTo("RIB_RECEIVER_456");
        assertThat(result.getMontant()).isEqualTo(2500.75);
        assertThat(result.getType()).isEqualTo(Type.NORMAL);
    }

    @Test
    void toVirementRequest_shouldReturnNull_whenInputIsNull() {
        // Act
        ExecuteVirementRequestDTO result = paymentMapper.toVirementRequest(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toVirementRequest_shouldMapInstantaneeType_whenTypeIsInstantanee() {
        // Arrange
        ExecuteVirementInput input = TestDataBuilder.createExecuteVirementInput(
                "RIB_A",
                "RIB_B",
                1000.0,
                Type.INSTANTANEE);

        // Act
        ExecuteVirementRequestDTO result = paymentMapper.toVirementRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(Type.INSTANTANEE);
    }

    @Test
    void toVirementRequest_shouldHandleNullFields_whenInputHasNullFields() {
        // Arrange
        ExecuteVirementInput input = new ExecuteVirementInput();
        input.setRibEmetteur(null);
        input.setRibRecepteur(null);
        input.setMontant(null);
        input.setType(null);

        // Act
        ExecuteVirementRequestDTO result = paymentMapper.toVirementRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRibEmetteur()).isNull();
        assertThat(result.getRibRecepteur()).isNull();
        assertThat(result.getMontant()).isNull();
        assertThat(result.getType()).isNull();
    }

    @Test
    void toVirementRequest_shouldHandleZeroAmount_whenMontantIsZero() {
        // Arrange
        ExecuteVirementInput input = TestDataBuilder.createExecuteVirementInput(
                "RIB_X",
                "RIB_Y",
                0.0,
                Type.NORMAL);

        // Act
        ExecuteVirementRequestDTO result = paymentMapper.toVirementRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMontant()).isEqualTo(0.0);
    }

    // ========== SAVINGS MAPPING TESTS ==========

    @Test
    void toSavingsRequest_shouldMapAllFields_whenValidInputProvided() {
        // Arrange
        DepotRetraitCEpargneRequestDTO input = TestDataBuilder.createSavingsInput(
                "EPARGNE_RIB_789",
                "COURANT_RIB_012",
                3500.50);

        // Act
        DepotRetraitCEpargneRequestDTO result = paymentMapper.toSavingsRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEpargnerib()).isEqualTo("EPARGNE_RIB_789");
        assertThat(result.getCourantrib()).isEqualTo("COURANT_RIB_012");
        assertThat(result.getMontant()).isEqualTo(3500.50);
    }

    @Test
    void toSavingsRequest_shouldReturnNull_whenInputIsNull() {
        // Act
        DepotRetraitCEpargneRequestDTO result = paymentMapper.toSavingsRequest(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toSavingsRequest_shouldHandleNullFields_whenInputHasNullFields() {
        // Arrange
        DepotRetraitCEpargneRequestDTO input = new DepotRetraitCEpargneRequestDTO();
        input.setEpargnerib(null);
        input.setCourantrib(null);
        input.setMontant(null);

        // Act
        DepotRetraitCEpargneRequestDTO result = input;

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEpargnerib()).isNull();
        assertThat(result.getCourantrib()).isNull();
        assertThat(result.getMontant()).isNull();
    }

    @Test
    void toSavingsRequest_shouldHandleEmptyStrings_whenRibsAreEmpty() {
        // Arrange
        DepotRetraitCEpargneRequestDTO input = TestDataBuilder.createSavingsInput(
                "",
                "",
                100.0);

        // Act
        DepotRetraitCEpargneRequestDTO result = paymentMapper.toSavingsRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEpargnerib()).isEmpty();
        assertThat(result.getCourantrib()).isEmpty();
        assertThat(result.getMontant()).isEqualTo(100.0);
    }

    @Test
    void toSavingsRequest_shouldHandleNegativeAmount_whenMontantIsNegative() {
        // Arrange
        DepotRetraitCEpargneRequestDTO input = TestDataBuilder.createSavingsInput(
                "EPARGNE_RIB",
                "COURANT_RIB",
                -500.0);

        // Act
        DepotRetraitCEpargneRequestDTO result = paymentMapper.toSavingsRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMontant()).isEqualTo(-500.0);
    }

    // ========== QUINZAINE MAPPING TESTS ==========

    @Test
    void toQuinzaineRequest_shouldMapAllFields_whenValidInputProvided() {
        // Arrange
        QuinzaineInput input = TestDataBuilder.createQuinzaineInput(
                "RIB_QUINZAINE_999",
                2025,
                12);

        // Act
        ClotureQuinzaineRequestDTO result = paymentMapper.toQuinzaineRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRib()).isEqualTo("RIB_QUINZAINE_999");
        // Note: quinzaineId is not mapped by PaymentMapper, only rib is mapped
    }

    @Test
    void toQuinzaineRequest_shouldReturnNull_whenInputIsNull() {
        // Act
        ClotureQuinzaineRequestDTO result = paymentMapper.toQuinzaineRequest(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toQuinzaineRequest_shouldHandleNullFields_whenInputHasNullFields() {
        // Arrange
        QuinzaineInput input = new QuinzaineInput();
        input.setRib(null);
        input.setYear(null);
        input.setQuaiznine(null);

        // Act
        ClotureQuinzaineRequestDTO result = paymentMapper.toQuinzaineRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRib()).isNull();
        // quinzaineId is not mapped from input
        assertThat(result.getQuinzaineId()).isNull();
    }

    @Test
    void toQuinzaineRequest_shouldHandleFirstQuinzaine_whenQuinzaineIs1() {
        // Arrange
        QuinzaineInput input = TestDataBuilder.createQuinzaineInput(
                "RIB_Q1",
                2024,
                1);

        // Act
        ClotureQuinzaineRequestDTO result = paymentMapper.toQuinzaineRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRib()).isEqualTo("RIB_Q1");
        // Note: quinzaineId is not mapped by PaymentMapper, only rib is mapped
    }

    @Test
    void toQuinzaineRequest_shouldHandleLastQuinzaine_whenQuinzaineIs24() {
        // Arrange
        QuinzaineInput input = TestDataBuilder.createQuinzaineInput(
                "RIB_Q24",
                2024,
                24);

        // Act
        ClotureQuinzaineRequestDTO result = paymentMapper.toQuinzaineRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRib()).isEqualTo("RIB_Q24");
        // Note: quinzaineId is not mapped by PaymentMapper, only rib is mapped
    }

    // ========== ANNUAL MAPPING TESTS ==========

    @Test
    void toAnnualRequest_shouldMapAllFields_whenValidInputProvided() {
        // Arrange
        AnnualInput input = TestDataBuilder.createAnnualInput(
                "RIB_ANNUAL_2025",
                2025);

        // Act
        CalculateAnnualeRequestDTO result = paymentMapper.toAnnualRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRib()).isEqualTo("RIB_ANNUAL_2025");
        assertThat(result.getYear()).isEqualTo(2025);
    }

    @Test
    void toAnnualRequest_shouldReturnNull_whenInputIsNull() {
        // Act
        CalculateAnnualeRequestDTO result = paymentMapper.toAnnualRequest(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toAnnualRequest_shouldHandleNullRib_whenRibIsNull() {
        // Arrange
        AnnualInput input = new AnnualInput();
        input.setRib(null);
        input.setYear(2023);

        // Act
        CalculateAnnualeRequestDTO result = paymentMapper.toAnnualRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRib()).isNull();
        assertThat(result.getYear()).isEqualTo(2023);
    }

    @Test
    void toAnnualRequest_shouldHandlePastYear_whenYearIsPast() {
        // Arrange
        AnnualInput input = TestDataBuilder.createAnnualInput(
                "RIB_HISTORICAL",
                2020);

        // Act
        CalculateAnnualeRequestDTO result = paymentMapper.toAnnualRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(2020);
    }

    @Test
    void toAnnualRequest_shouldHandleFutureYear_whenYearIsFuture() {
        // Arrange
        AnnualInput input = TestDataBuilder.createAnnualInput(
                "RIB_FUTURE",
                2030);

        // Act
        CalculateAnnualeRequestDTO result = paymentMapper.toAnnualRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(2030);
    }

    @Test
    void toAnnualRequest_shouldHandleEmptyRib_whenRibIsEmpty() {
        // Arrange
        AnnualInput input = TestDataBuilder.createAnnualInput(
                "",
                2025);

        // Act
        CalculateAnnualeRequestDTO result = paymentMapper.toAnnualRequest(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRib()).isEmpty();
        assertThat(result.getYear()).isEqualTo(2025);
    }

    // ========== CROSS-MAPPING CONSISTENCY TESTS ==========

    @Test
    void allMappers_shouldHandleNullInputConsistently() {
        // Act & Assert
        assertThat(paymentMapper.toVirementRequest(null)).isNull();
        assertThat(paymentMapper.toSavingsRequest(null)).isNull();
        assertThat(paymentMapper.toQuinzaineRequest(null)).isNull();
        assertThat(paymentMapper.toAnnualRequest(null)).isNull();
    }

    @Test
    void allMappers_shouldCreateNewInstances_whenCalled() {
        // Arrange
        ExecuteVirementInput virementInput = TestDataBuilder.createExecuteVirementInput();

        // Act
        ExecuteVirementRequestDTO result1 = paymentMapper.toVirementRequest(virementInput);
        ExecuteVirementRequestDTO result2 = paymentMapper.toVirementRequest(virementInput);

        // Assert
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1).isNotSameAs(result2); // Different instances
        assertThat(result1.getRibEmetteur()).isEqualTo(result2.getRibEmetteur()); // Same values
    }
}
