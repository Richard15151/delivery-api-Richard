package com.deliverytech.delivery_api.dto.requests;

import com.deliverytech.delivery_api.validation.TelefoneValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ClienteRequest", description = "Objeto de transferência para criação ou atualização de um cliente")
public class ClienteDTO {

    @Schema(
        description = "Nome completo do cliente", 
        example = "Richard Oliveira", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Campo nome é obrigatório.")
    private String nome;

    @Schema(
        description = "Endereço de e-mail único para login e comunicações", 
        example = "richard@gmail.com", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Email(message = "E-mail inválido.")
    @NotBlank(message = "Campo e-mail é obrigatório.")
    private String email;

    @Schema(
        description = "Telefone de contato formatado", 
        example = "(11) 98765-4321", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @TelefoneValido
    @NotBlank(message = "Campo telefone é obrigatório.")
    private String telefone;

    @Schema(
        description = "Endereço residencial para entregas", 
        example = "Rua das Flores, 123 - Centro", 
        minLength = 5, 
        maxLength = 255,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Size(min = 5, message = "Endereço deve ter no mínimo 5 caracteres")
    private String endereco;
}