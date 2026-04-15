package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.deliverytech.delivery_api.dto.requests.RestauranteDTO;
import com.deliverytech.delivery_api.dto.responses.PagedResponse;
import com.deliverytech.delivery_api.dto.responses.RestauranteResponseDTO;
import com.deliverytech.delivery_api.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/restaurantes", produces = "application/json")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento de estabelecimentos, busca por categoria e cálculo de taxas.")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar novo restaurante", description = "Registra um restaurante parceiro. Retorna a URI do novo recurso no header Location.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Restaurante cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflito: Nome já existente")
    })
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Valid @RequestBody RestauranteDTO dados) {
        RestauranteResponseDTO response = service.cadastrar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.getId()).toUri();
        
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Listar restaurantes ativos (Paginado)")
    @GetMapping
    public ResponseEntity<PagedResponse<RestauranteResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(new PagedResponse<>(service.listarAtivos(pageable)));
    }

    @Operation(summary = "Buscar restaurante por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucesso"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar restaurantes por nome", description = "Busca textual simples pelo nome do estabelecimento.")
    @GetMapping("/pesquisar/{nome}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @Operation(summary = "Filtrar por categoria (Paginado)")
    @GetMapping("/categoria")
    public ResponseEntity<PagedResponse<RestauranteResponseDTO>> buscarPorCategoria(
            @RequestParam String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PagedResponse<>(service.buscarPorCategoria(categoria, pageable)));
    }

    @Operation(summary = "Ver ranking de restaurantes", description = "Lista os restaurantes com melhor avaliação ou volume de vendas.")
    @GetMapping("/ranking")
    public ResponseEntity<List<RestauranteResponseDTO>> verRanking() {
        return ResponseEntity.ok(service.listarRanking());
    }

    @Operation(summary = "Alternar status (Ativar/Inativar)")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<RestauranteResponseDTO> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }

    @Operation(summary = "Atualizar dados do restaurante")
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Calcular taxa de entrega por CEP")
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxa(
            @Parameter(description = "ID do restaurante") @PathVariable Long id, 
            @Parameter(description = "CEP de destino") @PathVariable String cep) {
        return ResponseEntity.ok(service.calcularTaxaEntrega(id, cep));
    }

@Operation(
        summary = "Listar restaurantes próximos", 
        description = "Busca estabelecimentos que atendem na região do CEP informado (baseado nos 5 primeiros dígitos)."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "CEP em formato inválido")
    })
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(
            @Parameter(description = "CEP de busca (apenas números ou com hífen)", example = "12345-678")
            @PathVariable String cep) {
                
        if (cep == null || cep.length() < 8) {
            return ResponseEntity.badRequest().build();
        }

        List<RestauranteResponseDTO> proximos = service.buscarProximos(cep);
        return ResponseEntity.ok(proximos);
    }

    @Operation(summary = "Remover restaurante")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}