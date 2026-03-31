package com.deliverytech.delivery_api.controller;

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

import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.service.RestauranteService;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {
    private RestauranteService service;

    public RestauranteController ( RestauranteService service){
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Restaurante> cadastrar(@RequestBody Restaurante restaurante){
        return ResponseEntity.status(201).body(service.cadastrar(restaurante));
    }

    @GetMapping
    public List<Restaurante> listarAtivos(){
        return service.listarRanking();
    }

    @GetMapping("/{id}")
    public Restaurante buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @GetMapping("/pesquisar/{nome}")
    public List<Restaurante> buscarPorNome(@PathVariable String nome){
        return service.buscarPorNome(nome);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Restaurante> BuscarPorCategoria(@PathVariable String categoria) {
        return service.buscarPorCategoria(categoria);
    }

    @GetMapping("/ranking")
    public List<Restaurante> verRanking() {
        return service.listarRanking();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> trocarStatus(@PathVariable Long id) {
        service.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/atualizar-dados-restaurante")
    public Restaurante atualizar(@PathVariable Long id, @RequestBody Restaurante dados){
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}/deletar-restaurante")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); 
    }
}
