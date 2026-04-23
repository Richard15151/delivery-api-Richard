package com.deliverytech.delivery_api.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.PedidoDTO;
import com.deliverytech.delivery_api.dto.responses.PedidoResponseDTO;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
        summary = "Criar um novo pedido", 
        description = "Envia um carrinho para processamento. Valida estoque, disponibilidade do restaurante e calcula o valor total."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação, item indisponível ou restaurante fechado"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> criar(
            @RequestBody @Valid PedidoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.status(201).body(service.criarPedido(dto, usuarioLogado));
    }

    @Operation(summary = "Buscar pedido por ID", description = "Recupera os detalhes completos de um pedido, incluindo itens e status atual.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado com o ID informado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','RESTAURANTE')")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @PathVariable Long id, 
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(service.buscarPedidoPorId(id, usuarioLogado));
    }


    @Operation(
        summary = "Confirmar pedido", 
        description = "Ação realizada pelo Restaurante para aceitar o pedido. Altera o status de PENDENTE para CONFIRMADO."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido confirmado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Pedido não está em estado pendente ou já foi cancelado")
    })
    @PutMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('RESTAURANTE')")
    public ResponseEntity<PedidoResponseDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmarPedido(id));
    }

    @Operation(
        summary = "Avançar status da entrega", 
        description = "Move o pedido para a próxima etapa do fluxo logístico: CONFIRMADO -> PREPARANDO -> EM ENTREGA -> ENTREGUE."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "422", description = "Não é possível avançar a partir do status atual")
    })
    @PatchMapping("/{id}/status/avancar")
    @PreAuthorize("hasRole('RESTAURANTE')")
    public ResponseEntity<PedidoResponseDTO> avancarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarStatus(id));
    }

    @Operation(
        summary = "Cancelar pedido", 
        description = "Cancela um pedido ativo. Regra: Só pode ser cancelado se ainda não estiver em processo de PREPARAÇÃO."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para cancelar este pedido"),
        @ApiResponse(responseCode = "400", description = "Pedido já está em preparação ou finalizado")
    })
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE','RESTAURANTE')")
    public ResponseEntity<?> cancelar(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(service.cancelarPedido(id, usuarioLogado));
    }

    @Operation(summary = "Listar meus pedidos", description = "Retorna o histórico de pedidos do usuário atualmente autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos do usuário logado")
    })
    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> meusPedidos(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado,
            @Parameter(description = "Página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.meusPedidos(usuarioLogado, pageable));
    }
}