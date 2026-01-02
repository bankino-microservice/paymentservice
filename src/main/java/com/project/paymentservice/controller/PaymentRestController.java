package com.project.paymentservice.controller;

import com.project.paymentservice.model.dto.*;
import com.project.paymentservice.model.dto.event.PaymentEventMock;
import com.project.paymentservice.model.dto.inputs.*;
import com.project.paymentservice.model.mapper.PaymentMapper;
import com.project.paymentservice.service.LegacyRestClient;
import com.project.paymentservice.service.Paymentservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentRestController {

    private final LegacyRestClient legacyClient;
    private final PaymentMapper paymentMapper;
    private final Paymentservice paymentservice;

    public PaymentRestController(LegacyRestClient legacyClient,
                                 PaymentMapper paymentMapper, Paymentservice paymentservice) {
        this.legacyClient = legacyClient;
        this.paymentMapper = paymentMapper;
        this.paymentservice = paymentservice;
    }

    // ---------------- QUERIES ----------------

    @GetMapping("/virements/{id}")
    public ResponseEntity<VirementSoapInfoDTO> getVirementById(@PathVariable Long id) {
        return ResponseEntity.ok(
                legacyClient.getVirementById(id)
        );
    }

    // ---------------- MUTATIONS : VIREMENT ----------------

    @PostMapping("/virements/execute")
    public ResponseEntity<ExecuteVirementResponseDTO> executeVirement(
            @RequestBody ExecuteVirementInput input
    ) {
        ExecuteVirementRequestDTO request =
                paymentMapper.toVirementRequest(input);

        return ResponseEntity.ok(
                legacyClient.executeVirement(request)
        );
    }

    // ---------------- MUTATIONS : SAVINGS ----------------

    @PostMapping("/savings/deposit")
    public ResponseEntity<CEpargneResponseDTO> depositSavings(
            @RequestBody SavingsInput input
    ) {
        return ResponseEntity.ok(
                legacyClient.deposit(paymentMapper.toSavingsRequest(input))
        );
    }

    @PostMapping("/savings/withdraw")
    public ResponseEntity<CEpargneResponseDTO> withdrawSavings(
            @RequestBody SavingsInput input
    ) {
        return ResponseEntity.ok(
                legacyClient.withdraw(paymentMapper.toSavingsRequest(input))
        );
    }

    // ---------------- MUTATIONS : CLOSING ----------------

    @PostMapping("/closing/quinzaine")
    public ResponseEntity<ClotureAnnuelleResponseDTO> closeQuinzaine(
            @RequestBody QuinzaineInput input
    ) {
        return ResponseEntity.ok(
                legacyClient.closeQuinzaine(
                        paymentMapper.toQuinzaineRequest(input)
                )
        );
    }

    @PostMapping("/closing/annual")
    public ResponseEntity<ClotureAnnuelleResponseDTO> closeAnnual(
            @RequestBody AnnualInput input
    ) {
        return ResponseEntity.ok(
                legacyClient.closeAnnual(
                        paymentMapper.toAnnualRequest(input)
                )
        );
    }
    @PostMapping("/static")
    public ResponseEntity<String> sendStaticPayment() {

        paymentservice.sendPaymentEvent(
                PaymentEventMock.STATIC_PAYMENT_EVENT
        );

        return ResponseEntity.ok(
                "Static payment event sent to NotificationService"
        );
    }
}
