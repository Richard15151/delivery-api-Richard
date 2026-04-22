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
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.dto.responses.PedidoResponseDTO;
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
    public ResponseEntity<?> criar(
            @RequestBody @Valid PedidoDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(201)
                .body(service.criarPedido(dto, usuarioLogado));
    }

    @Operation(summary = "Buscar pedido por ID", description = "Recupera os detalhes completos de um pedido, incluindo itens e status atual.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado com o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @Parameter(description = "ID do pedido", example = "101") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPedidoPorId(id));
    }

    @Operation(summary = "Listar histórico do cliente", description = "Retorna todos os pedidos realizados por um cliente específico de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Histórico recuperado com sucesso")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<PagedResponse<PedidoResponseDTO>> listarPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId,
            @Parameter(description = "Página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PagedResponse<>(service.listarPorCliente(clienteId, pageable)));
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
    public ResponseEntity<PedidoResponseDTO> confirmar(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
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
    public ResponseEntity<PedidoResponseDTO> avancarStatus(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
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
    public ResponseEntity<?> cancelar(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(service.cancelarPedido(id, usuarioLogado));
    }

    @Operation(summary = "Filtrar pedidos por período", description = "Busca pedidos realizados entre duas datas e horas específicas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos no período",
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = PedidoResponseDTO.class))))
    })
    @GetMapping("/periodo")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorPeriodo(
            @Parameter(description = "Data inicial (ISO 8601)", example = "2026-04-01T00:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data final (ISO 8601)", example = "2026-04-30T23:59:59") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(service.buscarPorPeriodo(inicio, fim));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Listar meus pedidos", description = "Retorna o histórico de pedidos do usuário atualmente autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos do usuário logado")
    })
    @GetMapping("/meus")
    public ResponseEntity<?> meusPedidos(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado,
            @Parameter(description = "Página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.meusPedidos(usuarioLogado, pageable));
    }
}