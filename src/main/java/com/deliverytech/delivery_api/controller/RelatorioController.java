package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Endpoints analíticos para monitoramento de faturamento, estatísticas e performance de vendas.")
public class RelatorioController {

    private final PedidoService pedidoService;

    public RelatorioController(PedidoService pedidoService) { 
        this.pedidoService = pedidoService; 
    }

    @Operation(summary = "Relatório: Faturamento total", description = "Calcula a soma financeira de todos os pedidos concluídos dentro do intervalo de tempo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Faturamento calculado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Formato de data inválido.")
    })
    @GetMapping("/faturamento")
    public ResponseEntity<BigDecimal> getFaturamento(
            @Parameter(description = "Data e hora de início (ISO 8601)", example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            
            @Parameter(description = "Data e hora de fim (ISO 8601)", example = "2026-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(pedidoService.obterFaturamentoTotal(inicio, fim));
    }

    @Operation(summary = "Relatório: Quantidade por status", description = "Retorna o volume total de pedidos para um status específico (ex: PENDENTE, ENTREGUE).")
    @GetMapping("/estatisticas")
    public ResponseEntity<Long> getQuantidadePorStatus(
            @Parameter(description = "Status do pedido para contagem") @RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.contarPedidosPorStatus(status));
    }

    @Operation(summary = "Relatório: Ranking de produtos", description = "Lista os produtos mais vendidos com base na quantidade total de itens em pedidos finalizados.")
    @GetMapping("/ranking-produtos")
    public ResponseEntity<List<Object[]>> getRanking() {
        return ResponseEntity.ok(pedidoService.obterRankingProdutos());
    }

    @Operation(summary = "Vendas por período", description = "Endpoint alternativo para consulta rápida de faturamento por período.")
    @GetMapping("/vendas-por-periodo")
    public ResponseEntity<BigDecimal> faturamento(
            @Parameter(description = "Data de início", example = "2026-04-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            
            @Parameter(description = "Data de fim", example = "2026-04-30T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(pedidoService.obterFaturamentoTotal(inicio, fim));
    }
}