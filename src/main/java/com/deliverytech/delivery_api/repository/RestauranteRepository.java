package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.enums.CategoriaRestaurante;
import com.deliverytech.delivery_api.model.Restaurante;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long>{
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNome(String nome);
    Page<Restaurante> findByAtivoTrue(Pageable pageable);
    Page<Restaurante> findByCategoriaAndAtivoTrue(CategoriaRestaurante categoria, Pageable pageable);
    List<Restaurante> findByAtivoTrueOrderByAvaliacaoDesc();
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxaEntrega);
    List<Restaurante> findTop5ByOrderByNomeAsc();
    List<Restaurante> findByEnderecoContaining(String prefixo);
}
