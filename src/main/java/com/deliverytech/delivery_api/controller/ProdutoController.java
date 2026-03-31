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

import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private ProdutoService service;

    public ProdutoController ( ProdutoService service){
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Produto> cadastrar(@RequestBody Produto produto){
        return ResponseEntity.status(201).body(service.cadastrar(produto));
    }

    @GetMapping
    public List<Produto> listarDisponiveis(){
        return service.listarDisponiveis();
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @GetMapping("/restaurante/{id}")
    public List<Produto> listarPorRestaurante(@PathVariable Long id) {
        return service.buscarPorRestaurante(id);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public List<Produto> listarPorCategoria(@PathVariable String nomeCategoria) {
        return service.buscarPorCategoria(nomeCategoria);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> trocarStatus(@PathVariable Long id) {
        service.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/atualizar-dados-produto")
    public Produto atualizar(@PathVariable Long id, @RequestBody Produto dados){
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}/deletar-produto")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
