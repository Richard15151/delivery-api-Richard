package com.deliverytech.delivery_api.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {
    @NotBlank (message = "Campo nome é obrigatório.")
    private String nome;

    @Email(message = "E-mail inválido.")
    @NotBlank (message = "Campo e-mail é obrigatório.")
    private String email;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-\\d{4}$", message = "Formato de telefone inválido")
    @NotBlank (message = "Campo telefone é obrigatório.")
    private String telefone;

    @Size(min =10, message="Endereço deve ter no mínimo 10 caracteres.")
    private String endereco;
}
