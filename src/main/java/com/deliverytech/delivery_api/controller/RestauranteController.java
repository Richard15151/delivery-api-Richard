package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.deliverytech.delivery_api.dto.requests.RestauranteDTO;
import com.deliverytech.delivery_api.dto.responses.RestauranteResponseDTO;
import com.deliverytech.delivery_api.service.RestauranteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {
    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Valid @RequestBody RestauranteDTO dto) {
        return ResponseEntity.status(201).body(service.cadastrarRestaurante(dto));
    }

    @GetMapping
    public List<RestauranteResponseDTO> listarAtivos(){
        return service.listarAtivos();
    }

    @GetMapping("/{id}")
    public RestauranteResponseDTO buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @GetMapping("/pesquisar/{nome}")
    public List<RestauranteResponseDTO> buscarPorNome(@PathVariable String nome){
        return service.buscarPorNome(nome);
    }

    @GetMapping("/categoria/{categoria}")
    public List<RestauranteResponseDTO> BuscarPorCategoria(@PathVariable String categoria) {
        return service.buscarPorCategoria(categoria);
    }

    @GetMapping("/ranking")
    public List<RestauranteResponseDTO> verRanking() {
        return service.listarRanking();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> trocarStatus(@PathVariable Long id) {
        service.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public RestauranteResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteDTO dto) {
        return service.atualizar(id, dto);
    }

    @GetMapping("/{id}/taxa-entrega/{cep}")
    public BigDecimal calcularTaxa(@PathVariable Long id, @PathVariable String cep) {
        return service.calcularTaxaEntrega(id, cep);
    }

    @DeleteMapping("/{id}/deletar-restaurante")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); 
    }
}
