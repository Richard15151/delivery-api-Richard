package com.deliverytech.delivery_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
        return service.listarAtivos();
    }

    @GetMapping("/{id}")
    public Restaurante buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}/inativar-restaurante")
    public void inativar(@PathVariable Long id){
        service.inativar(id);
    }

    @PutMapping("/{id}/ativar-restaurante")
    public void ativar(@PathVariable Long id){
        service.ativar(id);
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
