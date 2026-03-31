package com.deliverytech.delivery_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.model.StatusPedido;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;

    // Injeção de todos os repositórios necessários
    public PedidoService(PedidoRepository repository, ProdutoRepository produtoRepository, 
                         ClienteRepository clienteRepository, RestauranteRepository restauranteRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        if (!clienteRepository.existsById(pedido.getCliente().getId())) {
            throw new RuntimeException("Cliente não encontrado.");
        }
        if (!restauranteRepository.existsById(pedido.getRestaurante().getId())) {
            throw new RuntimeException("Restaurante não encontrado.");
        }

        BigDecimal valorTotalItens = BigDecimal.ZERO;

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));

            item.setPrecoUnitario(produto.getPreco());
            
            BigDecimal subtotal = item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade()));
            item.setSubtotal(subtotal);
            
            item.setPedido(pedido);
            
            valorTotalItens = valorTotalItens.add(subtotal);
        }

        pedido.setValorTotal(valorTotalItens.add(pedido.getTaxaEntrega()));
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setNumeroPedido(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return repository.save(pedido);
    }

    public List<Pedido> buscarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public List<Pedido> buscarPorStatus(StatusPedido status){
        return repository.findByStatus(status);
    }

    public List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
    return repository.findByDataPedidoBetween(inicio, fim);
    }

    public List<Pedido> buscarPorData(LocalDateTime inicio, LocalDateTime fim){
        return repository.findByDataPedidoBetween(inicio, fim);
    }
    
    public Pedido atualizarStatus(Long id, StatusPedido novoStatus) {
    Pedido pedido = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    pedido.setStatus(novoStatus);
    return repository.save(pedido);
    }

    public BigDecimal obterFaturamentoTotal(LocalDateTime inicio, LocalDateTime fim) {
        BigDecimal total = repository.calcularTotalVendido(inicio, fim);
        return (total != null) ? total : BigDecimal.ZERO;
    }

    public Long contarPedidosPorStatus(StatusPedido status) {
        return repository.countByStatus(status);
    }

    public List<Object[]> obterRankingProdutos() {
        return repository.buscarProdutosMaisVendidos();
    }
}