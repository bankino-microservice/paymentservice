package com.project.paymentservice.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetVirementsEmisResponseDTO {
    @JsonProperty("virementsEmis")
    @JsonAlias({ "virements", "virementsEmis" })
    private List<VirementDTO> virementsEmis = new ArrayList<>();
}
