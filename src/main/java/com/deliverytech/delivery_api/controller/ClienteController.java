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

    @Operation(summary="Cadastrar novo cliente.")
        @ApiResponses(
            value={
                @ApiResponse(responseCode="201", description="Clientes Cadastrado com sucesso."),
                @ApiResponse(responseCode="400", description="Erro de validação."),
            }
        )
        @PostMapping("/cadastrar")
        public ResponseEntity<ClienteResponseDTO> cadastrar(
                @Valid @RequestBody ClienteDTO dto,
                @AuthenticationPrincipal Usuario logado) {

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(service.cadastrar(dto, logado));
        }


    @Operation(summary="Buscar cliente por Id.")
    @ApiResponses(
        value={
            @ApiResponse(responseCode="200", description="Cliente encontrado com sucesso."),
            @ApiResponse(responseCode="404", description="Cliente não encontrado com Id mencionado."),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<ClienteResponseDTO>> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok().header("Content-Type", "application/json").body(new com.deliverytech.delivery_api.dto.responses.ApiResponse<>( service.buscarPorId(id)));  
    }

    @Operation(summary="Listar clientes ativos.")
    @ApiResponses(
        value={
            @ApiResponse(responseCode="200", description="Lista de clientes ativos retornado."),
            @ApiResponse(responseCode="404", description="Cliente não encontrado."),
        }
    )
    @GetMapping
    public ResponseEntity<PagedResponse<ClienteResponseDTO>> listarAtivos(
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10")int size
    ){

        Pageable pageable = PageRequest.of(page, size);
        var pageResponse = new PagedResponse<>(service.listarAtivos(pageable));

        return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
        
        .header("Content-Type", "application/json").body(pageResponse);

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