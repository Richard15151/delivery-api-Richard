package com.deliverytech.delivery_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliverytech.delivery_api.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNomeAndRestauranteId(String nome, Long restauranteId);
    List<Produto> findByDisponivelTrue();
    List<Produto> findByRestauranteId(Long restauranteId);
    List<Produto> findByCategoriaIgnoreCase(String categoria);
}
