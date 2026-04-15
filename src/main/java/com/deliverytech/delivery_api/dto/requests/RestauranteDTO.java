package com.deliverytech.delivery_api.dto.requests;

import java.math.BigDecimal;

import com.deliverytech.delivery_api.validation.CategoriaValida;
import com.deliverytech.delivery_api.validation.TelefoneValido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "RestauranteRequest", description = "Objeto de transferência para criação ou atualização de um Restaurante")
public class RestauranteDTO {

    @Schema(
        description = "Nome completo do restaurante", 
        example = "Bayano Sushi", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Campo nome é obrigatório")
    private String nome;

    @Schema(
        description = "Categoria do restaurante", 
        example = "Comida Japonesa", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Categoria é obrigatória")
    @CategoriaValida
    private String categoria;

    @Schema(
        description = "Endereço residencial para entregas", 
        example = "Rua das Flores, 123 - Centro", 
        minLength = 5, 
        maxLength = 255
    )
    @Size(min = 5, message = "Endereço deve ter no mínimo 5 caracteres")
    private String endereco;

    @Schema(
        description = "Telefone de contato formatado", 
        example = "(11) 98765-4321", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @TelefoneValido
    @NotBlank(message = "Campo telefone é obrigatório.")
    private String telefone;

    @Schema(
        description = "Taxa de entrega do restaurante", 
        example = "14.99", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "A taxa de entrega é obrigatória")
    private BigDecimal taxaEntrega;
}