package com.project.paymentservice.service;

import com.project.paymentservice.configuration.FeignOAuth2Config;
import com.project.paymentservice.model.dto.ExecuteVirementRequestDTO;
import com.project.paymentservice.model.dto.ExecuteVirementResponseDTO;
import com.project.paymentservice.model.dto.GetClientIdByRibRequestDTO;
import com.project.paymentservice.model.dto.GetClientIdByRibResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service"
        , url = "${account.service}",
        configuration = FeignOAuth2Config.class

)

public interface Compteclient {
    @PostMapping("/api/accounts/client-id/by-rib")
    public ResponseEntity<GetClientIdByRibResponseDTO> getClientIdByRib(
             @RequestBody GetClientIdByRibRequestDTO request);
}
