package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.PedidoDTO;
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.dto.responses.PedidoResponseDTO;
import com.deliverytech.delivery_api.dto.responses.ApiResponse;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/pedidos", produces = "application/json")
@Tag(name = "Pedidos", description = "Endpoints para fluxo de compras.")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Criar um novo pedido.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201", 
        description = "Pedido criado com sucesso."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> criar(@RequestBody @Valid PedidoDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>(pedidoService.criarPedido(dto)));
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPedidoPorId(id);
    }

    @Operation(summary = "Listar histórico de pedidos do cliente (paginado).")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<PagedResponse<PedidoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PagedResponse<>(pedidoService.listarPorCliente(clienteId, pageable)));
    }

    @Operation(summary = "Confirmar um pedido (Aceite do restaurante).")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Pedido confirmado com sucesso."
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Pedido não está em estado PENDENTE."
        )
    })
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> confirmar(@PathVariable Long id) {
        var resultado = pedidoService.confirmarPedido(id);
        return ResponseEntity.ok(new ApiResponse<>(resultado));
    }


    @Operation(summary = "Avançar o status do pedido (Fluxo: CONFIRMADO -> PREPARANDO -> ENTREGA).")
    @PatchMapping("/{id}/status/avancar")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> avancarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(pedidoService.atualizarStatus(id)));
    }

    @Operation(summary = "Cancelar um pedido.")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(pedidoService.cancelarPedido(id)));
    }

    @GetMapping("/periodo")
    public List<PedidoResponseDTO> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return pedidoService.buscarPorPeriodo(inicio, fim);
    }

    @GetMapping("/relatorios/faturamento")
    public ResponseEntity<BigDecimal> getFaturamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(pedidoService.obterFaturamentoTotal(inicio, fim));
    }

    @GetMapping("/relatorios/estatisticas")
    public ResponseEntity<Long> getQuantidadePorStatus(@RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.contarPedidosPorStatus(status));
    }

    @GetMapping("/relatorios/ranking-produtos")
    public ResponseEntity<List<Object[]>> getRanking() {
        return ResponseEntity.ok(pedidoService.obterRankingProdutos());
    }
}