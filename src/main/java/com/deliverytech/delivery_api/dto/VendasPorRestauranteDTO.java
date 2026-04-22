package com.deliverytech.delivery_api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "VendasPorRestaurante", description = "Objeto de transferência para consultas de informações de vendas por restaurante")
public class VendasPorRestauranteDTO {

    @Schema(
        description = "Nome completo do restaurante", 
        example = "Bayano Sushi", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Campo nome é obrigatório")
    private String nomeRestaurante;

    @Schema(
        description = "Total de vendas", 
        example = "49"
    )
    private BigDecimal totalVendas;

    public VendasPorRestauranteDTO(String nomeRestaurante, BigDecimal totalVendas) {
        this.nomeRestaurante = nomeRestaurante;
        this.totalVendas = totalVendas;
    }

    @Schema(
        description = "Busca o nome do restaurante", 
        example = "Campeiros Grill"
    )
    public String getNomeRestaurante() {
        return nomeRestaurante;
    }

    @Schema(
        description = "Busca a quantidade de vendas", 
        example = "49"
    )
    public BigDecimal getTotalVendas() {
        return totalVendas;
    }
}