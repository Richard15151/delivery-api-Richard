package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.StatusPedido;
import com.deliverytech.delivery_api.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody Pedido pedido) {
        Pedido novoPedido = service.criarPedido(pedido);
        return ResponseEntity.status(201).body(novoPedido);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> listarPorCliente(@PathVariable Long clienteId) {
        return service.buscarPorCliente(clienteId);
    }

    @GetMapping("/status/{status}")
    public List<Pedido> listarPorStatus(@PathVariable StatusPedido status) {
        return service.buscarPorStatus(status);
    }

    @GetMapping("/periodo")
    public List<Pedido> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return service.buscarPorPeriodo(inicio, fim);
    }

    @GetMapping("/data")
    public List<Pedido> listarPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return service.buscarPorData(inicio, fim);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long id, 
            @RequestBody StatusPedido novoStatus) {
        
        Pedido pedidoAtualizado = service.atualizarStatus(id, novoStatus);
        return ResponseEntity.ok(pedidoAtualizado);
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