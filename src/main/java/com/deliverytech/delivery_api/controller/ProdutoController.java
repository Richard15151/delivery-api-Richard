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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/produtos", produces = "application/json")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento do cardápio, categorias e disponibilidade de itens.")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANTE')")
    @Operation(
        summary = "Cadastrar novo produto", 
        description = "Vincula um novo produto ao cardápio de um restaurante específico. Requer permissão de ADMIN ou do próprio RESTAURANTE."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para gerenciar este restaurante"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PostMapping("/restaurante/{restauranteId}")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<ProdutoResponseDTO>> cadastrar(
            @Parameter(description = "ID do restaurante proprietário do produto") @PathVariable Long restauranteId,
            @RequestBody @Valid ProdutoDTO produto,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(
                        produtoService.cadastrar(restauranteId, produto, usuarioLogado)
                ));
    }

    @Operation(
        summary = "Listar todos os produtos disponíveis", 
        description = "Retorna uma lista global de todos os produtos que estão marcados como ativos/disponíveis no sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso",
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProdutoResponseDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarDisponiveis() {
        return ResponseEntity.ok(produtoService.listarDisponiveis());
    }

    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes técnicos e de preço de um item específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "ID do produto", example = "50") @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @Operation(summary = "Listar produtos por restaurante", description = "Retorna o cardápio paginado de um restaurante específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cardápio recuperado com sucesso")
    })
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<PagedResponse<ProdutoResponseDTO>> listarPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @Parameter(description = "Página atual") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PagedResponse<>(produtoService.listarPorRestaurante(restauranteId, pageable)));
    }

    @Operation(summary = "Buscar produtos por categoria", description = "Filtra itens do cardápio global por categoria (ex: Pizza, Bebidas, Sobremesas).")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria", example = "Lanches") @PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANTE')")
    @Operation(
        summary = "Alternar disponibilidade (Ativo/Inativo)", 
        description = "Habilita ou desabilita a visibilidade do produto no cardápio. Útil para itens sazonais ou falta de estoque imediata."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status de disponibilidade alterado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<ProdutoResponseDTO>> toggleDisponibilidade(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(
            new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(
                produtoService.toggleDisponibilidade(id, usuarioLogado)
            )
        );
    }

    @Operation(summary = "Atualizar dados do produto", description = "Permite alterar nome, descrição, preço ou categoria de um item existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "ID do produto") @PathVariable Long id, 
            @Valid @RequestBody ProdutoDTO dto) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, dto));
    }

    @Operation(summary = "Remover produto do cardápio", description = "Exclui permanentemente o registro do produto do sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}