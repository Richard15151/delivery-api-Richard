package com.deliverytech.delivery_api.dto.responses;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "ProdutoResponse", description = "Objeto que representa os dados de saída de um produto cadastrado.")
public class ProdutoResponseDTO {

    @Schema(description = "Identificador único do produto no banco de dados", example = "1")
    private Long id;

    @Schema(description = "Nome completo do produto", example = "Marmita de Feijoada")
    private String nome;

    @Schema(description = "categoria do produto", example = "Comida japonesa")
    private String categoria;

    @Schema(description = "Descrição do produto", example = "Marmita de 500g, serve uma pessoa")
    private String descricao;

    @Schema(description = "preço do produto", example = "19.99")
    private BigDecimal preco;

    @Schema(description = "Indica se o produto está disponível no sistema", example = "true")
    private boolean disponivel;

    @Schema(description = "Identificador único do restaurante no banco de dados", example = "1")
    private Long restauranteId;

    @Schema(description = "Nome completo do restaurante", example = "Bayano Sushi")
    private String restauranteNome;
}