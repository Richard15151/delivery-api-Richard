package com.deliverytech.delivery_api.dto.responses;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "RestauranteResponse", description = "Objeto que representa os dados de saída de um restaurante cadastrado.")
public class RestauranteResponseDTO {

    @Schema(description = "Identificador único do restaurante no banco de dados", example = "1")
    private Long id;

    @Schema(description = "Nome completo do restaurante", example = "Bayano Sushi")
    private String nome;

    @Schema(description = "Categoria do restaurante", example = "Comida Japonesa")
    private String categoria;

    @Schema(description = "Endereço completo do restaurante", example = "Rua das Flores, 123 - Centro")
    private String endereco;

    @Schema(description = "Telefone de contato", example = "(11) 98765-4321")
    private String telefone;

    @Schema(description = "Avaliação do restaurante", example = "9.5")
    private BigDecimal avaliacao;

    @Schema(description = "Taxa de entrega do restaurante", example = "14.99")
    private BigDecimal taxaEntrega;

    @Schema(description = "Indica se o restaurante está com o cadastro ativo no sistema", example = "true")
    private boolean ativo;

    public Boolean isAtivo(){
        return ativo;
    }
}