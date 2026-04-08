package com.deliverytech.delivery_api.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteDTO {
    @NotBlank(message = "Campo Nome é obrigatório.")
    private String nome;

    @NotBlank(message = "Campo Categoria é obrigatório.")
    private String categoria;

    @NotBlank(message = "Campo Endereço é obrigatório.")
    private String endereco;

    @Pattern(regexp="^\\(\\d{2}\\)\\d{4,5}-\\d{4}$",
        message="Formato de telefone inválido. Use (xx)xxxxx-xxxx"
    )
    @NotBlank(message="Campo telefone é obrigatório.")
    private String telefone;

    @NotBlank(message = "Campo Avaliação é obrigatório.")
    private BigDecimal avaliacao;

}
