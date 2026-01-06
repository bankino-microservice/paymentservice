package com.project.paymentservice.controller;

import com.project.paymentservice.model.dto.*;
import com.project.paymentservice.model.dto.event.PaymentEvent;
//
import com.project.paymentservice.model.dto.inputs.*;
import com.project.paymentservice.model.dto.response.GetAllVirementsClientResponseDTO;
import com.project.paymentservice.model.dto.response.GetVirementsEmisResponseDTO;
import com.project.paymentservice.model.dto.response.GetVirementsRecusResponseDTO;
import com.project.paymentservice.model.mapper.PaymentMapper;
import com.project.paymentservice.service.Compteclient;
import com.project.paymentservice.service.LegacyRestClient;
import com.project.paymentservice.service.Paymentservice;
import com.project.paymentservice.service.Userclient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentRestController {
    private final Compteclient compteclient;
    private final LegacyRestClient legacyClient;
    private final PaymentMapper paymentMapper;
    private final Paymentservice paymentservice;
    private final Userclient userclient;

    public PaymentRestController(LegacyRestClient legacyClient,
            PaymentMapper paymentMapper, Paymentservice paymentservice, Compteclient compteclient,
            Userclient userclient) {
        this.legacyClient = legacyClient;
        this.paymentMapper = paymentMapper;
        this.paymentservice = paymentservice;
        this.compteclient = compteclient;
        this.userclient = userclient;
    }

    // ---------------- QUERIES ----------------

    @GetMapping("/virements/{id}")
    public ResponseEntity<VirementSoapInfoDTO> getVirementById(@PathVariable Long id) {
        return ResponseEntity.ok(
                legacyClient.getVirementById(id));
    }

    @GetMapping("/virements/client/{clientRib}")
    public ResponseEntity<GetAllVirementsClientResponseDTO> getAllVirementsClient(@PathVariable String clientRib) {
        return ResponseEntity.ok(
                legacyClient.getAllVirementsClient(clientRib));
    }

    @GetMapping("/virements/emis/{clientRib}")
    public ResponseEntity<GetVirementsEmisResponseDTO> getVirementsEmis(@PathVariable String clientRib) {
        return ResponseEntity.ok(
                legacyClient.getVirementsEmis(clientRib));
    }

    @GetMapping("/virements/recus/{clientRib}")
    public ResponseEntity<GetVirementsRecusResponseDTO> getVirementsRecus(@PathVariable String clientRib) {
        return ResponseEntity.ok(
                legacyClient.getVirementsRecus(clientRib));
    }

    // ---------------- MUTATIONS : VIREMENT ----------------

    @PostMapping("/virements/execute")
    public ResponseEntity<ExecuteVirementResponseDTO> executeVirement(
            @RequestBody ExecuteVirementInput input) {
        // 1. Map the input
        ExecuteVirementRequestDTO request = paymentMapper.toVirementRequest(input);
        ExecuteVirementResponseDTO responseBody = legacyClient.executeVirement(request);

        // 3. Validate the response from the legacy system
        if (responseBody != null) {
            PaymentEvent paymentEvent=new PaymentEvent();
            GetClientIdByRibRequestDTO requestDTO = new GetClientIdByRibRequestDTO(input.getRibRecepteur());
            GetClientIdByRibResponseDTO responseDTO = compteclient.getClientIdByRib(requestDTO).getBody();
            RegisterClientResDTO registerClientResDTO=userclient.get(responseDTO.getClientId()).getBody();
            paymentEvent.setAmount(BigDecimal.valueOf(input.getMontant()));
            paymentEvent.setClientName(registerClientResDTO.getFirstName());
            paymentEvent.setClientEmail(registerClientResDTO.getEmail());
            paymentEvent.setClientPhoneNumber(registerClientResDTO.getPhone());
            paymentEvent.setDescription("Virement");


            System.out.println("rib"+responseDTO.getClientId());

            paymentservice.sendPaymentEvent(paymentEvent);

            // 5. Return success (200 OK or 201 CREATED based on your requirement)
            return ResponseEntity
                    .status(HttpStatus.CREATED) // Explicitly setting CREATED if that is the requirement
                    .body(responseBody);
        } else {
            // 6. Handle failure gracefully
            throw new RuntimeException("Legacy system returned null response");
        }
    }
    // ---------------- MUTATIONS : SAVINGS ----------------

    @PostMapping("/savings/deposit")
    public ResponseEntity<CEpargneResponseDTO> depositSavings(
            @RequestBody DepotRetraitCEpargneRequestDTO input
    ) {
        System.out.println(input.getCourantrib());
        // 1. Map the input


        // 2. Call the legacy client
        CEpargneResponseDTO responseBody = legacyClient.deposit(input);

        // 3. Validate the response
        if (responseBody != null) {


//            // 5. Return success
            return ResponseEntity.ok(responseBody);
        } else {
            // 6. Handle failure gracefully
            throw new RuntimeException("Legacy system returned null response for Savings Deposit");
        }
    }

    @PostMapping("/savings/withdraw")
    public ResponseEntity<CEpargneResponseDTO> withdrawSavings(
            @RequestBody DepotRetraitCEpargneRequestDTO input
    ) {
        // 1. Map the input


        // 2. Call the legacy client
        CEpargneResponseDTO responseBody = legacyClient.withdraw(input);

        // 3. Validate the response
        if (responseBody != null) {
         
            return ResponseEntity.ok(responseBody);
        } else {
            // 6. Handle failure gracefully
            throw new RuntimeException("Legacy system returned null response for Savings Withdrawal");
        }
    }

    // ---------------- MUTATIONS : CLOSING ----------------

    @PostMapping("/closing/quinzaine")
    public ResponseEntity<ClotureAnnuelleResponseDTO> closeQuinzaine(
            @RequestBody QuinzaineInput input) {
        return ResponseEntity.ok(
                legacyClient.closeQuinzaine(
                        paymentMapper.toQuinzaineRequest(input)));
    }

    @PostMapping("/closing/annual")
    public ResponseEntity<ClotureAnnuelleResponseDTO> closeAnnual(
            @RequestBody AnnualInput input) {
        return ResponseEntity.ok(
                legacyClient.closeAnnual(
                        paymentMapper.toAnnualRequest(input)));
    }

    @PostMapping("/static")
    public ResponseEntity<String> sendStaticPayment() {

        // paymentservice.sendPaymentEvent(
        // PaymentEventMock.STATIC_PAYMENT_EVENT
        // );

        return ResponseEntity.ok(
                "Static payment event sent to NotificationService");
    }
}
