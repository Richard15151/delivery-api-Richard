package com.deliverytech.delivery_api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "itemPedido", description = "Objeto de transferência para consultas de informações de um pedido cadastrado")
public interface ItemPedidoDTO {
    @Schema(
        description = "Busca o nome do produto", 
        example = "Poke de salmão"
    )
    String getNomeProduto();

    @Schema(
        description = "Busca a quantidade pedida do produto", 
        example = "3"
    )
    Integer getQuantidade();

    @Schema(
        description = "Busca o subtotal do pedido", 
        example = "93.50"
    )
    BigDecimal getSubtotal();
}