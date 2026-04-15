package com.deliverytech.delivery_api.repository;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dto.VendasPorRestauranteDTO;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByStatus(StatusPedido status);
    @Query("SELECT DISTINCT p FROM Pedido p " +
       "JOIN FETCH p.cliente " +
       "JOIN FETCH p.itens i " +
       "JOIN FETCH i.produto " +
       "WHERE p.dataPedido BETWEEN :inicio AND :fim")
    List<Pedido> findByDataPedidoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    List<Pedido> findByClienteIdAndStatus(Long clienteId, StatusPedido status);
    List<Pedido> findTop10ByOrderByDataPedidoDesc();
    
    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalVendido(LocalDateTime inicio, LocalDateTime fim);

    Long countByStatus(StatusPedido status);

    @Query("SELECT i.produto.nome, SUM(i.quantidade) as total FROM ItemPedido i " +
       "GROUP BY i.produto.nome ORDER BY total DESC")
    List<Object[]> buscarProdutosMaisVendidos();

     @Query(value = """
        SELECT DISTINCT p FROM Pedido p
        JOIN FETCH p.cliente
        JOIN FETCH p.restaurante
        LEFT JOIN FETCH p.itens i
        LEFT JOIN FETCH i.produto
        WHERE p.cliente.id = :clienteId
        """, 
        countQuery = "SELECT count(p) FROM Pedido p WHERE p.cliente.id = :clienteId")
    Page<Pedido> buscarItensPorClientes(@Param("clienteId") Long clienteId, Pageable pageable);

    @Query("""
            SELECT p FROM Pedido p
            WHERE p.dataPedido  BETWEEN :inicio AND :fim
    """)
    List<Pedido> findByDateTime(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );


    @Query("""
        select new com.deliverytech.delivery_api.dto.VendasPorRestauranteDTO(
            r.nome,
            coalesce(sum(ip.subtotal), 0)
        )
        from Pedido p
        join p.restaurante r
        join p.itens ip
        group by r.nome
    """)
            List<VendasPorRestauranteDTO> buscarVendasPorRestaurante();

            @Query(value="""
                        SELECT c.nome AS cliente, COUNT(p.id) AS total_pedidos
                        FROM pedidos p 
                        JOIN clientes c ON c.id = p.cliente_id
                        GROUP BY c.nome
                        ORDER BY total_pedidos DESC
                """, nativeQuery = true )
            List<Object[]> rankingClientes();

    @Query("SELECT p FROM Pedido p " +
       "JOIN FETCH p.cliente " +
       "JOIN FETCH p.itens i " +
       "JOIN FETCH i.produto " + 
       "WHERE p.id = :id")
    Optional<Pedido> buscarCompletoPorId(@Param("id") Long id);
}