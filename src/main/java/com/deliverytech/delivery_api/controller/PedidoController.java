package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.PedidoDTO;
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.dto.responses.PedidoResponseDTO;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/pedidos", produces = "application/json")
@Tag(name = "Pedidos", description = "Endpoints para processamento de ordens de compra e gestão do fluxo de entrega.")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Criar um novo pedido", description = "Envia um carrinho para processamento. Valida estoque e disponibilidade do restaurante.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro nos dados enviados ou item indisponível")
    })
    @PostMapping
    public ResponseEntity<?> criar(
            @RequestBody @Valid PedidoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(201)
                .body(service.criarPedido(dto, usuarioLogado));
    }

    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPedidoPorId(id));
    }

    @Operation(summary = "Listar histórico do cliente (Paginado)")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<PagedResponse<PedidoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PagedResponse<>(service.listarPorCliente(clienteId, pageable)));
    }

    @Operation(summary = "Confirmar pedido", description = "Muda o status do pedido de PENDENTE para CONFIRMADO (Aceite do Restaurante).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido confirmado"),
        @ApiResponse(responseCode = "400", description = "O pedido não está mais pendente")
    })
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<PedidoResponseDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmarPedido(id));
    }

    @Operation(summary = "Avançar status da entrega", description = "Move o pedido pelo fluxo: CONFIRMADO -> PREPARANDO -> EM ENTREGA -> ENTREGUE.")
    @PatchMapping("/{id}/status/avancar")
    public ResponseEntity<PedidoResponseDTO> avancarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarStatus(id));
    }

    @Operation(summary = "Cancelar pedido", description = "Cancela o pedido. Só pode ser feito antes do status PREPARANDO.")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(service.cancelarPedido(id, usuarioLogado));
    }

    @Operation(summary = "Filtrar pedidos por período")
    @GetMapping("/periodo")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(service.buscarPorPeriodo(inicio, fim));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @GetMapping("/meus")
    public ResponseEntity<?> meusPedidos(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.meusPedidos(usuarioLogado, pageable));
    }
}