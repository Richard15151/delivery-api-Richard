package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.deliverytech.delivery_api.dto.requests.RestauranteDTO;
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.dto.responses.RestauranteResponseDTO;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/restaurantes", produces = "application/json")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento de estabelecimentos, busca por categoria e logística de entrega.")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @Operation(
        summary = "Cadastrar novo restaurante", 
        description = "Cria um perfil de restaurante vinculado ao usuário logado. Retorna a URL do novo recurso no cabeçalho 'Location'."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restaurante cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de validação incorretos"),
        @ApiResponse(responseCode = "409", description = "Já existe um restaurante cadastrado com este nome")
    })
    @PostMapping
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<RestauranteResponseDTO>> cadastrar(
                @Valid @RequestBody RestauranteDTO dados,
                @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado) {

                RestauranteResponseDTO response = service.cadastrar(dados, usuarioLogado);

                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.getId())
                        .toUri();

                return ResponseEntity.created(location)
                        .body(new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(response));
        }

    @Operation(summary = "Listar restaurantes ativos", description = "Retorna uma lista paginada de todos os restaurantes operacionais. Possui cache de 60 segundos.")
    @GetMapping("/listar")
    public ResponseEntity<PagedResponse<RestauranteResponseDTO>> listar(
        @Parameter(description = "Página (0..N)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = service.listarAtivos(pageable);
        var response = new PagedResponse<>(pageResult);
        
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(response);
    }

    @Operation(summary = "Buscar restaurante por ID", description = "Recupera os detalhes de um estabelecimento específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}/buscar-restaurante-por-id")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<RestauranteResponseDTO>> buscarPorId(
            @Parameter(description = "ID único do restaurante") @PathVariable Long id) {
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(service.buscarPorId(id)));
    }

    @Operation(summary = "Pesquisar por nome", description = "Busca restaurantes que contenham o termo informado no nome.")
    @GetMapping("/pesquisar/{nome}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorNome(
            @Parameter(description = "Parte do nome do restaurante", example = "Burguer") @PathVariable String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @Operation(summary = "Filtrar por categoria", description = "Lista restaurantes pertencentes a uma categoria específica (ex: Japonesa, Pizza).")
    @GetMapping("/categoria")
    public ResponseEntity<PagedResponse<RestauranteResponseDTO>> buscarPorCategoria(
        @Parameter(description = "Nome da categoria") @RequestParam String categoria,
        @Parameter(description = "Página") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = service.buscarPorCategoria(categoria, pageable);
        var response = new PagedResponse<>(pageResult);
        
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(response);
    }

    @Operation(summary = "Ranking de qualidade", description = "Retorna os estabelecimentos melhor avaliados pela comunidade.")
    @GetMapping("/ranking")
    public ResponseEntity<List<RestauranteResponseDTO>> verRanking() {
        return ResponseEntity.ok(service.listarRanking());
    }

    @Operation(summary = "Alternar status de operação", description = "Ativa ou desativa o restaurante. Requer que o usuário logado seja o proprietário.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Usuário não é o proprietário deste restaurante")
    })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<com.deliverytech.delivery_api.dto.responses.ApiResponse<RestauranteResponseDTO>> toggle(
                @Parameter(description = "ID do restaurante") @PathVariable Long id,
                @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado) {

                return ResponseEntity.ok(
                        new com.deliverytech.delivery_api.dto.responses.ApiResponse<>(service.toggle(id, usuarioLogado))
                );
        }

    @Operation(summary = "Atualizar restaurante", description = "Atualiza dados como endereço, telefone e especialidade.")
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id, 
            @Valid @RequestBody RestauranteDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Calcular frete", description = "Estima o valor da taxa de entrega baseado na distância entre o restaurante e o CEP de destino.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cálculo realizado", content = @Content(schema = @Schema(implementation = BigDecimal.class, example = "7.50")))
    })
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxa(
            @Parameter(description = "ID do restaurante") @PathVariable Long id, 
            @Parameter(description = "CEP de destino", example = "01234-567") @PathVariable String cep) {
        return ResponseEntity.ok(service.calcularTaxaEntrega(id, cep));
    }

    @Operation(
        summary = "Listar restaurantes por proximidade", 
        description = "Busca estabelecimentos em um raio geográfico baseado nos primeiros dígitos do CEP informado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de próximos encontrada"),
        @ApiResponse(responseCode = "400", description = "CEP inválido ou incompleto")
    })
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(
            @Parameter(description = "CEP para busca", example = "12345-678") @PathVariable String cep) {
                
        if (cep == null || cep.length() < 8) {
            return ResponseEntity.badRequest().build();
        }

        List<RestauranteResponseDTO> proximos = service.buscarProximos(cep);
        return ResponseEntity.ok(proximos);
    }

    @Operation(summary = "Remover restaurante", description = "Exclui o registro do restaurante e todos os seus produtos vinculados.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removido com sucesso")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do restaurante") @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}