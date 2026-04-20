package com.deliverytech.delivery_api.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.requests.ProdutoDTO;
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.dto.responses.ProdutoResponseDTO;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/produtos", produces = "application/json")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento do cardápio e disponibilidade de itens.")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANTE')")
    @Operation(summary = "Cadastrar novo produto", description = "Vincula um novo produto a um restaurante específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PostMapping("/restaurante/{restauranteId}")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<ProdutoResponseDTO>> cadastrar(
             @PathVariable Long restauranteId,
                @RequestBody @Valid ProdutoDTO produto,
                @AuthenticationPrincipal Usuario usuarioLogado) {

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(
                                produtoService.cadastrar(restauranteId, produto, usuarioLogado)
                        ));
        }   


    @Operation(summary = "Listar todos os produtos disponíveis", description = "Retorna uma lista simples de todos os itens ativos no sistema.")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarDisponiveis() {
        return ResponseEntity.ok(produtoService.listarDisponiveis());
    }

    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @Operation(summary = "Listar produtos por restaurante (Paginado)")
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<PagedResponse<ProdutoResponseDTO>> listarPorRestaurante(
            @PathVariable Long restauranteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PagedResponse<>(produtoService.listarPorRestaurante(restauranteId, pageable)));
    }

    @Operation(summary = "Buscar produtos por categoria")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANTE')")
    @Operation(summary = "Alternar disponibilidade", description = "Ativa ou desativa um produto para venda no cardápio.")
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<ProdutoResponseDTO>> toggleDisponibilidade(
    @PathVariable Long id,
    @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(
            new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(
                produtoService.toggleDisponibilidade(id, usuarioLogado)
            )
        );
}

    @Operation(summary = "Atualizar dados do produto")
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoDTO dto) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, dto));
    }

    @Operation(summary = "Remover produto")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto deletado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}