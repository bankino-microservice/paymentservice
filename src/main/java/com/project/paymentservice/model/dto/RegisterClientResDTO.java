package com.project.paymentservice.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterClientResDTO {

    private String firstName;
    private String lastName;
    private String job;
    private String phone;
    private String username;
    private String email;

}
