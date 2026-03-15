package com.skct.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailCodeRequest {

    @NotBlank
    @Email
    private String email;
}
