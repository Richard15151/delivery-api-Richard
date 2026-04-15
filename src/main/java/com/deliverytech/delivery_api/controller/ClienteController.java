package com.deliverytech.delivery_api.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.ClienteDTO;
import com.deliverytech.delivery_api.dto.responses.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gerenciamento de clientes da plataforma")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar novo cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@Valid @RequestBody ClienteDTO dto) {
        ClienteResponseDTO resultado = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO resultado = service.buscarPorId(id);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Listar clientes ativos (Paginado)")
    @GetMapping
    public ResponseEntity<PagedResponse<ClienteResponseDTO>> listarAtivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PagedResponse<>(service.listarAtivos(pageable)));
    }

    @Operation(summary = "Alternar status (Ativo/Inativo)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResponseDTO> alternarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.alternarStatus(id));
    }

    @Operation(summary = "Atualizar dados do cliente")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @Operation(summary = "Remover cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}