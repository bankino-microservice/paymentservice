package com.project.paymentservice.service;

import com.project.paymentservice.configuration.FeignOAuth2Config;
import com.project.paymentservice.model.dto.RegisterClientResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",
        url = "${user.service}",
        configuration = FeignOAuth2Config.class)
public interface Userclient {

    @GetMapping("/api/users/clients/get/{id}")
    public ResponseEntity<RegisterClientResDTO> get(@PathVariable Long id);

}
