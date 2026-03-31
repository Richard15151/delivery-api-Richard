package com.deliverytech.delivery_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;

import com.deliverytech.delivery_api.repository.RestauranteRepository;

@Service
public class RestauranteService {
     private RestauranteRepository repository;

     public RestauranteService (RestauranteRepository repository){
        this.repository = repository;
    }

    public Restaurante cadastrar(Restaurante restaurante){
        if (repository.existsByNome(restaurante.getNome())){
            throw new IllegalArgumentException("Esse nome de restaurante já está em uso.");
        }
        restaurante.setAtivo(true);
        return repository.save(restaurante);
    }

    public List<Restaurante> listarAtivos(){
        return repository.findByAtivoTrue();
    }

    public Restaurante buscarPorId(Long id){
        return repository.findById(id).orElseThrow(()-> new IllegalArgumentException("Restaurante não encontrado."));
    }

    public List<Restaurante> buscarPorNome(String nome){
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Restaurante> buscarPorCategoria(String categoria) {
        return repository.findByCategoriaContainingIgnoreCase(categoria);
    }

    public List<Restaurante> listarRanking() {
        return repository.findByAtivoTrueOrderByAvaliacaoDesc();
    }

    public void alternarStatus(Long id) {
        Restaurante restaurante = buscarPorId(id);
        restaurante.setAtivo(!restaurante.getAtivo());
        repository.save(restaurante);
    }

    public Restaurante atualizar(Long id, Restaurante dados){
        Restaurante restaurante = buscarPorId(id);
        restaurante.setNome(dados.getNome());
        restaurante.setCategoria(dados.getCategoria());
        restaurante.setEndereco(dados.getEndereco());
        restaurante.setTelefone(dados.getTelefone());
        restaurante.setAvaliacao(dados.getAvaliacao());
        return repository.save(restaurante);
    }

    public void deletar(Long id){
        if (!repository.existsById(id)) {
        throw new IllegalArgumentException("Restaurante não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }

}
