package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/relatorios")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Relatórios", description = "Endpoints analíticos para monitoramento de faturamento, estatísticas e performance de vendas.")
public class RelatorioController {

    private final PedidoService pedidoService;

    public RelatorioController(PedidoService pedidoService) { 
        this.pedidoService = pedidoService; 
    }

    @Operation(
        summary = "Faturamento total por período", 
        description = "Calcula a soma financeira de todos os pedidos concluídos dentro do intervalo de tempo informado. Utiliza o padrão ISO 8601."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Faturamento calculado com sucesso", 
                     content = @Content(schema = @Schema(implementation = BigDecimal.class, example = "1550.50"))),
        @ApiResponse(responseCode = "400", description = "Parâmetros de data inválidos ou malformados")
    })
    @GetMapping("/faturamento")
    public ResponseEntity<BigDecimal> getFaturamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.ok(pedidoService.obterFaturamentoTotal(inicio, fim, logado));
    }

    @Operation(
        summary = "Volume de pedidos por status", 
        description = "Retorna a contagem total de pedidos baseada no status (ex: PENDENTE, ENTREGUE, CANCELADO)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Status fornecido é inválido")
    })
    @GetMapping("/estatisticas")
    public ResponseEntity<Long> getQuantidadePorStatus(
            @Parameter(description = "Status do pedido para filtragem", schema = @Schema(implementation = StatusPedido.class)) 
            @RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.contarPedidosPorStatus(status));
    }

    @Operation(
        summary = "Ranking de produtos mais vendidos", 
        description = "Lista os produtos com maior saída, retornando o nome do produto e a quantidade total vendida em pedidos finalizados."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking gerado com sucesso",
                     content = @Content(array = @ArraySchema(schema = @Schema(type = "array", example = "['Pizza Margherita', 45]"))))
    })
    @GetMapping("/ranking-produtos")
    public ResponseEntity<List<Object[]>> getRanking() {
        return ResponseEntity.ok(pedidoService.obterRankingProdutos());
    }
}