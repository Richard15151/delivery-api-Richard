package com.deliverytech.delivery_api.repository;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> findByClienteIdAndStatus(Long clienteId, StatusPedido status);
    
    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalVendido(LocalDateTime inicio, LocalDateTime fim);

    Long countByStatus(StatusPedido status);

    @Query("SELECT i.produto.nome, SUM(i.quantidade) as total FROM ItemPedido i " +
       "GROUP BY i.produto.nome ORDER BY total DESC")
    List<Object[]> buscarProdutosMaisVendidos();
}