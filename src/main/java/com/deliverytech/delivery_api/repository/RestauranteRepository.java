package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deliverytech.delivery_api.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long>{
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNome(String nome);
    Page<Restaurante> findByAtivoTrue(Pageable pageable);
    Page<Restaurante> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);
    List<Restaurante> findByAtivoTrueOrderByAvaliacaoDesc();
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxaEntrega);
    List<Restaurante> findTop5ByOrderByNomeAsc();
}
