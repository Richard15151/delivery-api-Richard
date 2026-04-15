package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.deliverytech.delivery_api.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    Page<Produto> findByCategoria(String categoria, Pageable pageable);
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNomeAndRestauranteId(String nome, Long restauranteId);
    @Query("SELECT p FROM Produto p JOIN FETCH p.restaurante WHERE p.disponivel = true")
    List<Produto> findByDisponivelTrue();
    List<Produto> findByRestauranteId(Long restauranteId);
    @Query("SELECT p FROM Produto p JOIN FETCH p.restaurante WHERE UPPER(p.categoria) = UPPER(:categoria)")
    List<Produto> findByCategoriaIgnoreCase(@Param("categoria") String categoria);
    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);
    @Query("SELECT p FROM Produto p JOIN FETCH p.restaurante WHERE p.id = :id")
    Optional<Produto> buscarComRestaurante(@Param("id") Long id);
    @Query("SELECT p FROM Produto p JOIN FETCH p.restaurante WHERE p.restaurante.id = :restauranteId AND p.disponivel = true")
    Page<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId, Pageable pageable);
}
