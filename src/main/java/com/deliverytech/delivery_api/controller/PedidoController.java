package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.PedidoDTO;
import com.deliverytech.delivery_api.dto.responses.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@Valid @RequestBody PedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPedido(dto));
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPedidoPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<PedidoResponseDTO> listarPorCliente(@PathVariable Long clienteId) {
        return service.buscarPorCliente(clienteId);
    }

    @GetMapping("/status/{status}")
    public List<PedidoResponseDTO> listarPorStatus(@PathVariable StatusPedido status) {
        return service.buscarPorStatus(status);
    }

    @GetMapping("/periodo")
    public List<PedidoResponseDTO> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return service.buscarPorPeriodo(inicio, fim);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id, @RequestBody StatusPedido status) {
        return ResponseEntity.ok(service.atualizarStatus(id, status));
    }

    @GetMapping("/relatorios/faturamento")
    public ResponseEntity<BigDecimal> getFaturamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(service.obterFaturamentoTotal(inicio, fim));
    }

    @GetMapping("/relatorios/estatisticas")
    public ResponseEntity<Long> getQuantidadePorStatus(@RequestParam StatusPedido status) {
        return ResponseEntity.ok(service.contarPedidosPorStatus(status));
    }

    @GetMapping("/relatorios/ranking-produtos")
    public ResponseEntity<List<Object[]>> getRanking() {
        return ResponseEntity.ok(service.obterRankingProdutos());
    }
}