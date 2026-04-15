package com.deliverytech.delivery_api.dto.responses;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedidoResponseDTO {

    @Schema(description = "Nome completo do produto", example = "Marmita de Feijoada")
    private String nomeProduto;

    @Schema(description = "Quantidade do produto pedido", example = "3")
    private Integer quantidade;

    @Schema(description = "Preço unitário do produto", example = "19,99")
    private BigDecimal precoUnitario;

    @Schema(description = "Subtotal do pedido", example = "59,97")
    private BigDecimal subtotal;
}