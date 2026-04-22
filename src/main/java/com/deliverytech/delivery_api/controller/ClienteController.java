package com.deliverytech.delivery_api.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.ClienteDTO;
import com.deliverytech.delivery_api.dto.responses.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de perfis de clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @Operation(
        summary = "Cadastrar um novo cliente", 
        description = "Vincula um perfil de cliente ao usuário autenticado. Requer um token JWT válido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos ou cliente já cadastrado para este usuário"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para esta operação")
    })
    @PostMapping("/cadastrar")
    public ResponseEntity<ClienteResponseDTO> cadastrar(
            @Valid @RequestBody ClienteDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario logado) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.cadastrar(dto, logado));
    }

    @Operation(summary = "Buscar cliente por ID", description = "Retorna os detalhes de um cliente específico através do seu identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID fornecido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<ClienteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(service.buscarPorId(id)));
    }

    @Operation(summary = "Listar clientes ativos de forma paginada", description = "Retorna uma lista de clientes que possuem status ativo no sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    })
    @GetMapping
    public ResponseEntity<PagedResponse<ClienteResponseDTO>> listarAtivos(
            @Parameter(description = "Número da página (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResponse = new PagedResponse<>(service.listarAtivos(pageable));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .header("Content-Type", "application/json")
                .body(pageResponse);
    }

    @Operation(summary = "Alternar status do cliente", description = "Ativa ou desativa um cliente (Soft Delete).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResponseDTO> alternarStatus(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        return ResponseEntity.ok(service.alternarStatus(id));
    }

    @Operation(summary = "Atualizar dados cadastrais", description = "Atualiza informações como nome, telefone ou endereço do cliente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados fornecidos são inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @Parameter(description = "ID do cliente") @PathVariable Long id, 
            @Valid @RequestBody ClienteDTO dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @Operation(summary = "Excluir permanentemente um cliente", description = "Remove o registro do cliente do banco de dados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}