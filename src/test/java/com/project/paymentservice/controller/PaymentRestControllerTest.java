package com.project.paymentservice.controller;

import com.project.paymentservice.model.dto.*;
import com.project.paymentservice.model.dto.event.PaymentEvent;
import com.project.paymentservice.model.dto.inputs.*;
import com.project.paymentservice.model.enums.Type;
import com.project.paymentservice.model.mapper.PaymentMapper;
import com.project.paymentservice.service.Compteclient;
import com.project.paymentservice.service.LegacyRestClient;
import com.project.paymentservice.service.Paymentservice;
import com.project.paymentservice.service.Userclient;
import com.project.paymentservice.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentRestController.
 * Tests all endpoints with mocked dependencies to verify orchestration logic,
 * error handling, and integration between services.
 */
@ExtendWith(MockitoExtension.class)
class PaymentRestControllerTest {

    @Mock
    private LegacyRestClient legacyClient;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private Paymentservice paymentservice;

    @Mock
    private Compteclient compteclient;

    @Mock
    private Userclient userclient;

    @InjectMocks
    private PaymentRestController controller;

    // ========== Test Data ==========
    private ExecuteVirementInput virementInput;
    private ExecuteVirementRequestDTO virementRequestDTO;
    private ExecuteVirementResponseDTO virementResponseDTO;
    private DepotRetraitCEpargneRequestDTO savingsInput;
    private DepotRetraitCEpargneRequestDTO savingsRequestDTO;
    private CEpargneResponseDTO savingsResponseDTO;
    private QuinzaineInput quinzaineInput;
    private ClotureQuinzaineRequestDTO quinzaineRequestDTO;
    private ClotureAnnuelleResponseDTO quinzaineResponseDTO;
    private AnnualInput annualInput;
    private CalculateAnnualeRequestDTO annualRequestDTO;
    private ClotureAnnuelleResponseDTO annualResponseDTO;
    private GetClientIdByRibRequestDTO getClientIdRequest;
    private GetClientIdByRibResponseDTO getClientIdResponse;
    private RegisterClientResDTO clientDetails;

    @BeforeEach
    void setUp() {
        // Virement test data
        virementInput = TestDataBuilder.createExecuteVirementInput();
        virementRequestDTO = TestDataBuilder.createExecuteVirementRequestDTO();
        virementResponseDTO = TestDataBuilder.createExecuteVirementResponseDTO();

        // Savings test data
        savingsInput = TestDataBuilder.createSavingsInput();
        savingsRequestDTO = TestDataBuilder.createDepotRetraitCEpargneRequestDTO();
        savingsResponseDTO = TestDataBuilder.createCEpargneResponseDTO();

        // Quinzaine test data
        quinzaineInput = TestDataBuilder.createQuinzaineInput();
        quinzaineRequestDTO = TestDataBuilder.createClotureQuinzaineRequestDTO();
        quinzaineResponseDTO = TestDataBuilder.createClotureAnnuelleResponseDTO();

        // Annual test data
        annualInput = TestDataBuilder.createAnnualInput();
        annualRequestDTO = TestDataBuilder.createCalculateAnnualeRequestDTO();
        annualResponseDTO = TestDataBuilder.createClotureAnnuelleResponseDTO();

        // Client data
        getClientIdRequest = TestDataBuilder.createGetClientIdByRibRequestDTO("RIB987654321");
        getClientIdResponse = TestDataBuilder.createGetClientIdByRibResponseDTO(100L);
        clientDetails = TestDataBuilder.createRegisterClientResDTO();
    }

    // ========== GET VIREMENT BY ID TESTS ==========

    @Test
    void getVirementById_shouldReturnVirementInfo_whenIdExists() {
        // Arrange
        Long virementId = 1L;
        VirementSoapInfoDTO expectedVirement = new VirementSoapInfoDTO();
        expectedVirement.setId(virementId);
        expectedVirement.setCompteEmetteurRib("RIB123");
        expectedVirement.setCompteRecepteurRib("RIB456");
        expectedVirement.setMontant(1000.0);
        expectedVirement.setType("INSTANTANEE");

        when(legacyClient.getVirementById(virementId)).thenReturn(expectedVirement);

        // Act
        ResponseEntity<VirementSoapInfoDTO> response = controller.getVirementById(virementId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(virementId);
        assertThat(response.getBody().getCompteEmetteurRib()).isEqualTo("RIB123");
        verify(legacyClient, times(1)).getVirementById(virementId);
    }

    @Test
    void getVirementById_shouldReturnNull_whenLegacyClientReturnsNull() {
        // Arrange
        Long virementId = 999L;
        when(legacyClient.getVirementById(virementId)).thenReturn(null);

        // Act
        ResponseEntity<VirementSoapInfoDTO> response = controller.getVirementById(virementId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
        verify(legacyClient, times(1)).getVirementById(virementId);
    }

    // ========== EXECUTE VIREMENT TESTS ==========

    @Test
    void executeVirement_shouldReturnCreated_whenVirementIsSuccessful() {
        // Arrange
        when(paymentMapper.toVirementRequest(virementInput)).thenReturn(virementRequestDTO);
        when(legacyClient.executeVirement(virementRequestDTO)).thenReturn(virementResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        // Act
        ResponseEntity<ExecuteVirementResponseDTO> response = controller.executeVirement(virementInput);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(virementResponseDTO);
        verify(paymentMapper, times(1)).toVirementRequest(virementInput);
        verify(legacyClient, times(1)).executeVirement(virementRequestDTO);
        verify(compteclient, times(1)).getClientIdByRib(any(GetClientIdByRibRequestDTO.class));
        verify(userclient, times(1)).get(100L);
        verify(paymentservice, times(1)).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void executeVirement_shouldSendCorrectPaymentEvent_whenVirementIsSuccessful() {
        // Arrange
        when(paymentMapper.toVirementRequest(virementInput)).thenReturn(virementRequestDTO);
        when(legacyClient.executeVirement(virementRequestDTO)).thenReturn(virementResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);

        // Act
        controller.executeVirement(virementInput);

        // Assert
        verify(paymentservice).sendPaymentEvent(eventCaptor.capture());
        PaymentEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(virementInput.getMontant()));
        assertThat(capturedEvent.getClientName()).isEqualTo(clientDetails.getFirstName());
        assertThat(capturedEvent.getClientEmail()).isEqualTo(clientDetails.getEmail());
        assertThat(capturedEvent.getClientPhoneNumber()).isEqualTo(clientDetails.getPhone());
        assertThat(capturedEvent.getDescription()).isEqualTo("Virement");
    }

    @Test
    void executeVirement_shouldThrowRuntimeException_whenLegacyClientReturnsNull() {
        // Arrange
        when(paymentMapper.toVirementRequest(virementInput)).thenReturn(virementRequestDTO);
        when(legacyClient.executeVirement(virementRequestDTO)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> controller.executeVirement(virementInput))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Legacy system returned null response");

        verify(paymentservice, never()).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void executeVirement_shouldCallGetClientIdWithCorrectRib_whenVirementIsSuccessful() {
        // Arrange
        when(paymentMapper.toVirementRequest(virementInput)).thenReturn(virementRequestDTO);
        when(legacyClient.executeVirement(virementRequestDTO)).thenReturn(virementResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        ArgumentCaptor<GetClientIdByRibRequestDTO> ribCaptor = ArgumentCaptor
                .forClass(GetClientIdByRibRequestDTO.class);

        // Act
        controller.executeVirement(virementInput);

        // Assert
        verify(compteclient).getClientIdByRib(ribCaptor.capture());
        assertThat(ribCaptor.getValue().getRib()).isEqualTo(virementInput.getRibRecepteur());
    }

    // ========== DEPOSIT SAVINGS TESTS ==========

    @Test
    void depositSavings_shouldReturnOk_whenDepositIsSuccessful() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.deposit(savingsRequestDTO)).thenReturn(savingsResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        // Act
        ResponseEntity<CEpargneResponseDTO> response = controller.depositSavings(savingsInput);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(savingsResponseDTO);
        verify(paymentMapper, times(1)).toSavingsRequest(savingsInput);
        verify(legacyClient, times(1)).deposit(savingsRequestDTO);
        verify(compteclient, times(1)).getClientIdByRib(any(GetClientIdByRibRequestDTO.class));
        verify(userclient, times(1)).get(100L);
        verify(paymentservice, times(1)).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void depositSavings_shouldSendPaymentEventWithDepotDescription_whenDepositIsSuccessful() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.deposit(savingsRequestDTO)).thenReturn(savingsResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);

        // Act
        controller.depositSavings(savingsInput);

        // Assert
        verify(paymentservice).sendPaymentEvent(eventCaptor.capture());
        PaymentEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getDescription()).isEqualTo("Depot");
        assertThat(capturedEvent.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(savingsInput.getMontant()));
    }

    @Test
    void depositSavings_shouldThrowRuntimeException_whenLegacyClientReturnsNull() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.deposit(savingsRequestDTO)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> controller.depositSavings(savingsInput))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Legacy system returned null response for Savings Deposit");

        verify(paymentservice, never()).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void depositSavings_shouldUseCurrentRibForClientLookup_whenDepositIsSuccessful() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.deposit(savingsRequestDTO)).thenReturn(savingsResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        ArgumentCaptor<GetClientIdByRibRequestDTO> ribCaptor = ArgumentCaptor
                .forClass(GetClientIdByRibRequestDTO.class);

        // Act
        controller.depositSavings(savingsInput);

        // Assert
        verify(compteclient).getClientIdByRib(ribCaptor.capture());
        assertThat(ribCaptor.getValue().getRib()).isEqualTo(savingsInput.getCourantrib());
    }

    // ========== WITHDRAW SAVINGS TESTS ==========

    @Test
    void withdrawSavings_shouldReturnOk_whenWithdrawalIsSuccessful() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.withdraw(savingsRequestDTO)).thenReturn(savingsResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        // Act
        ResponseEntity<CEpargneResponseDTO> response = controller.withdrawSavings(savingsInput);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(savingsResponseDTO);
        verify(paymentMapper, times(1)).toSavingsRequest(savingsInput);
        verify(legacyClient, times(1)).withdraw(savingsRequestDTO);
        verify(compteclient, times(1)).getClientIdByRib(any(GetClientIdByRibRequestDTO.class));
        verify(userclient, times(1)).get(100L);
        verify(paymentservice, times(1)).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void withdrawSavings_shouldSendPaymentEventWithRetraitDescription_whenWithdrawalIsSuccessful() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.withdraw(savingsRequestDTO)).thenReturn(savingsResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(clientDetails));

        ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);

        // Act
        controller.withdrawSavings(savingsInput);

        // Assert
        verify(paymentservice).sendPaymentEvent(eventCaptor.capture());
        PaymentEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getDescription()).isEqualTo("Retrait");
        assertThat(capturedEvent.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(savingsInput.getMontant()));
    }

    @Test
    void withdrawSavings_shouldThrowRuntimeException_whenLegacyClientReturnsNull() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.withdraw(savingsRequestDTO)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> controller.withdrawSavings(savingsInput))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Legacy system returned null response for Savings Withdrawal");

        verify(paymentservice, never()).sendPaymentEvent(any(PaymentEvent.class));
    }

    // ========== CLOSE QUINZAINE TESTS ==========

    @Test
    void closeQuinzaine_shouldReturnOk_whenClosureIsSuccessful() {
        // Arrange
        when(paymentMapper.toQuinzaineRequest(quinzaineInput)).thenReturn(quinzaineRequestDTO);
        when(legacyClient.closeQuinzaine(quinzaineRequestDTO)).thenReturn(quinzaineResponseDTO);

        // Act
        ResponseEntity<ClotureAnnuelleResponseDTO> response = controller.closeQuinzaine(quinzaineInput);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(quinzaineResponseDTO);
        verify(paymentMapper, times(1)).toQuinzaineRequest(quinzaineInput);
        verify(legacyClient, times(1)).closeQuinzaine(quinzaineRequestDTO);
        verifyNoInteractions(paymentservice); // No event sent for quinzaine closure
    }

    @Test
    void closeQuinzaine_shouldReturnNullBody_whenLegacyClientReturnsNull() {
        // Arrange
        when(paymentMapper.toQuinzaineRequest(quinzaineInput)).thenReturn(quinzaineRequestDTO);
        when(legacyClient.closeQuinzaine(quinzaineRequestDTO)).thenReturn(null);

        // Act
        ResponseEntity<ClotureAnnuelleResponseDTO> response = controller.closeQuinzaine(quinzaineInput);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    // ========== CLOSE ANNUAL TESTS ==========

    @Test
    void closeAnnual_shouldReturnOk_whenClosureIsSuccessful() {
        // Arrange
        when(paymentMapper.toAnnualRequest(annualInput)).thenReturn(annualRequestDTO);
        when(legacyClient.closeAnnual(annualRequestDTO)).thenReturn(annualResponseDTO);

        // Act
        ResponseEntity<ClotureAnnuelleResponseDTO> response = controller.closeAnnual(annualInput);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(annualResponseDTO);
        verify(paymentMapper, times(1)).toAnnualRequest(annualInput);
        verify(legacyClient, times(1)).closeAnnual(annualRequestDTO);
        verifyNoInteractions(paymentservice); // No event sent for annual closure
    }

    @Test
    void closeAnnual_shouldReturnNullBody_whenLegacyClientReturnsNull() {
        // Arrange
        when(paymentMapper.toAnnualRequest(annualInput)).thenReturn(annualRequestDTO);
        when(legacyClient.closeAnnual(annualRequestDTO)).thenReturn(null);

        // Act
        ResponseEntity<ClotureAnnuelleResponseDTO> response = controller.closeAnnual(annualInput);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    // ========== SEND STATIC PAYMENT TESTS ==========

    @Test
    void sendStaticPayment_shouldReturnOk_whenCalled() {
        // Act
        ResponseEntity<String> response = controller.sendStaticPayment();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Static payment event sent to NotificationService");
        verifyNoInteractions(paymentservice); // Method is currently commented out
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    void executeVirement_shouldHandleNullResponseEntityBody_whenCompteclientReturnsNullBody() {
        // Arrange
        when(paymentMapper.toVirementRequest(virementInput)).thenReturn(virementRequestDTO);
        when(legacyClient.executeVirement(virementRequestDTO)).thenReturn(virementResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(null));

        // Act & Assert - Should throw NullPointerException due to missing null check
        assertThatThrownBy(() -> controller.executeVirement(virementInput))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void executeVirement_shouldHandleNullResponseEntityBody_whenUserclientReturnsNullBody() {
        // Arrange
        when(paymentMapper.toVirementRequest(virementInput)).thenReturn(virementRequestDTO);
        when(legacyClient.executeVirement(virementRequestDTO)).thenReturn(virementResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(null));

        // Act & Assert - Should throw NullPointerException due to missing null check
        assertThatThrownBy(() -> controller.executeVirement(virementInput))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void depositSavings_shouldHandleNullResponseEntityBody_whenCompteclientReturnsNullBody() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.deposit(savingsRequestDTO)).thenReturn(savingsResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(null));

        // Act & Assert
        assertThatThrownBy(() -> controller.depositSavings(savingsInput))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void withdrawSavings_shouldHandleNullResponseEntityBody_whenUserclientReturnsNullBody() {
        // Arrange
        when(paymentMapper.toSavingsRequest(savingsInput)).thenReturn(savingsRequestDTO);
        when(legacyClient.withdraw(savingsRequestDTO)).thenReturn(savingsResponseDTO);
        when(compteclient.getClientIdByRib(any(GetClientIdByRibRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(getClientIdResponse));
        when(userclient.get(100L)).thenReturn(ResponseEntity.ok(null));

        // Act & Assert
        assertThatThrownBy(() -> controller.withdrawSavings(savingsInput))
                .isInstanceOf(NullPointerException.class);
    }
}
