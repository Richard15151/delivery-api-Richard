package com.deliverytech.delivery_api.dto.requests;

import com.deliverytech.delivery_api.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {

    @Schema(
        description = "Endereço de e-mail único para login e comunicações", 
        example = "richard@gmail.com", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Email(message = "E-mail inválido.")
    @NotBlank(message = "Campo e-mail é obrigatório.")
    private String email;

    @Schema(
        description = "Senha do usuário para login", 
        example = "R24F*@#6FN", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Campo senha é obrigatório.")
    private String senha;

    @Schema(
        description = "Papel do usuário para login e comunicação", 
        example = "CLIENTE", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Campo role é obrigatório.")
    private Role role;
}