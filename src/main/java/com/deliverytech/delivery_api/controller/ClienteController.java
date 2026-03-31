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

import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private ClienteService service;

    public ClienteController ( ClienteService service){
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Cliente> cadastrar(@RequestBody Cliente cliente ){
        return ResponseEntity.status(201).body(service.cadastrar(cliente));
    }

    @GetMapping
    public List<Cliente> listarAtivos(){
        return service.listarAtivos();
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> trocarStatus(@PathVariable Long id) {
        service.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/atualizar-dados-clientes")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente dados){
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}/deletar-cliente")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); 
    }
}