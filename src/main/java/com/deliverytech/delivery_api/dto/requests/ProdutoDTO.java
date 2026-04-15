package com.deliverytech.delivery_api.dto.requests;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ProdutoRequest", description = "Dados para cadastro/atualização de produto.")
public class ProdutoDTO {

    @Schema(
        description = "Nome completo do produto", 
        example = "Poke de salmão", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Campo nome é obrigatório")
    private String nome;

    @Schema(description = "Descrição detalhada dos ingredientes.", example = "Pão brioche, carne 180g, queijo cheddar e bacon.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 5, message = "Descrição deve ter ao menos 5 caracteres")
    private String descricao;

    @Schema(
        description = "Categoria do produto", 
        example = "Comida Japonesa", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @Schema(
        description = "Preço do produto", 
        example = "Comida Japonesa", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Positive(message = "O preço deve ser maior que zero")
    @NotNull(message = "Preço é obrigatório")
    private BigDecimal preco;
}