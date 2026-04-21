package com.deliverytech.delivery_api.dto;

import java.math.BigDecimal;

public class VendasPorRestauranteDTO {
    private String nomeRestaurante;
    private BigDecimal totalVendas;

    public VendasPorRestauranteDTO(String nomeRestaurante, BigDecimal totalVendas) {
        this.nomeRestaurante = nomeRestaurante;
        this.totalVendas = totalVendas;
    }

    public String getNomeRestaurante() {
        return nomeRestaurante;
    }

    public BigDecimal getTotalVendas() {
        return totalVendas;
    }
}