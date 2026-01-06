package com.project.paymentservice.service;

import com.project.paymentservice.model.dto.*;
import com.project.paymentservice.model.dto.response.GetAllVirementsClientResponseDTO;
import com.project.paymentservice.model.dto.response.GetVirementsEmisResponseDTO;
import com.project.paymentservice.model.dto.response.GetVirementsRecusResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "legacy-adapter-service", url = "${legacy.service.url}")
public interface LegacyRestClient {

    // --- Virement Operations ---
    @PostMapping("/api/virements/execute")
    ExecuteVirementResponseDTO executeVirement(@RequestBody ExecuteVirementRequestDTO request);

    @GetMapping("/api/virements/{id}")
    VirementSoapInfoDTO getVirementById(@PathVariable("id") Long id);

    @GetMapping("/api/virements/client/{clientRib}")
    GetAllVirementsClientResponseDTO getAllVirementsClient(@PathVariable("clientRib") String clientRib);

    @GetMapping("/api/virements/emis/{clientRib}")
    GetVirementsEmisResponseDTO getVirementsEmis(@PathVariable("clientRib") String clientRib);

    @GetMapping("/api/virements/recus/{clientRib}")
    GetVirementsRecusResponseDTO getVirementsRecus(@PathVariable("clientRib") String clientRib);

    // --- Savings Operations ---
    @PostMapping("/api/savings/deposit")
    CEpargneResponseDTO deposit(@RequestBody DepotRetraitCEpargneRequestDTO request);

    @PostMapping("/api/savings/withdraw")
    CEpargneResponseDTO withdraw(@RequestBody DepotRetraitCEpargneRequestDTO request);

    // --- Closing Operations ---
    @PostMapping("/api/operations/close-quinzaine")
    ClotureAnnuelleResponseDTO closeQuinzaine(@RequestBody ClotureQuinzaineRequestDTO request);

    @PostMapping("/api/operations/close-annual")
    ClotureAnnuelleResponseDTO closeAnnual(@RequestBody CalculateAnnualeRequestDTO request);
}
