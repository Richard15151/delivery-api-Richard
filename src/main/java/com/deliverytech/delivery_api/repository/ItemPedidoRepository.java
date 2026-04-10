package com.deliverytech.delivery_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.deliverytech.delivery_api.dto.responses.ItemPedidoResponseDTO;

import com.deliverytech.delivery_api.dto.requests.ItemPedidoDTO;
import com.deliverytech.delivery_api.model.ItemPedido;

import org.springframework.stereotype.Repository;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long>{

    @Query("""
            SELECT new com.deliverytech.delivery_api.dto.responses.ItemPedidoResponseDTO(
                p.nome, 
                i.quantidade, 
                i.precoUnitario, 
                i.subtotal
            )
            FROM ItemPedido i
            JOIN i.produto p
            WHERE i.pedido.id = :pedidoId
    """)
    List<ItemPedidoResponseDTO> buscarItensPorPedido(@Param("pedidoId") Long pedidoId);
}