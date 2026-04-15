package com.deliverytech.delivery_api.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.enums.StatusPedido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoResponseDTO {

    @Schema(description = "Identificador único do pedido no banco de dados", example = "1")
    private Long id;

    @Schema(description = "Data e hora que o pedido foi realizado", example = "14/04/2026 18:28")
    private LocalDateTime dataPedido;

    @Schema(description = "Valor total da compra", example = "19,99")
    private BigDecimal valorTotal;

    @Schema(description = "Status do pedido no banco de dados", example = "Preparando")
    private StatusPedido status;

    @Schema(description = "Endereço da entrega do pedido", example = "Rua das Flores, 123 - Centro")
    private String enderecoEntrega;

    @Schema(description = "Nome completo do cliente", example = "Richard Oliveira")
    private String nomeCliente;

    @Schema(description = "Nome completo do restaurante", example = "Bayano Sushi")
    private String nomeRestaurante;

    @Schema(description = "Lista de itens do pedido", example = "Marmita, Coca-Cola")
    private List<ItemPedidoResponseDTO> itens;
}