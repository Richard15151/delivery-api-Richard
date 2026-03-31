package com.deliverytech.delivery_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliverytech.delivery_api.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long>{
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNome(String nome);
    List<Restaurante> findByAtivoTrue();
    List<Restaurante> findByCategoria(String categoria);
    List<Restaurante> findByAtivoTrueOrderByAvaliacaoDesc();
}
