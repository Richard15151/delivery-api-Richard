package com.deliverytech.delivery_api.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.dto.requests.PedidoDTO;
import com.deliverytech.delivery_api.dto.responses.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;

import com.deliverytech.delivery_api.dto.requests.ItemPedidoDTO;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ModelMapper mapper;

    
    public PedidoService(PedidoRepository repository, ProdutoRepository produtoRepository, 
                         ClienteRepository clienteRepository, RestauranteRepository restauranteRepository, ModelMapper mapper) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        if (!cliente.isAtivo()) throw new BusinessException("Cliente está inativo e não pode pedir.");

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));
        if (!restaurante.isAtivo()) throw new BusinessException("Restaurante está fechado no momento.");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
        pedido.setStatus(StatusPedido.PENDENTE);
        
        pedido.setDataPedido(LocalDateTime.now());

        BigDecimal taxa = restaurante.getTaxaEntrega() != null ? restaurante.getTaxaEntrega() : BigDecimal.ZERO;
        pedido.setTaxaEntrega(taxa);

        BigDecimal subtotalGeral = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto ID " + itemDto.getProdutoId() + " não existe."));
            
            if (!produto.isDisponivel()) throw new BusinessException("Produto " + produto.getNome() + " está indisponível.");

            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("Produto " + produto.getNome() + " não pertence ao restaurante " + restaurante.getNome());
            }

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            
            BigDecimal subtotalItem = produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
            item.setSubtotal(subtotalItem);
            item.setPedido(pedido);
            
            pedido.getItens().add(item);
            subtotalGeral = subtotalGeral.add(subtotalItem);
        }
        pedido.setValorTotal(subtotalGeral.add(pedido.getTaxaEntrega()));
        
        Pedido pedidoSalvo = repository.save(pedido);
        return converterParaDTO(pedidoSalvo);
    }

    public List<PedidoResponseDTO> buscarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public PedidoResponseDTO buscarPedidoPorId(Long id) {
    Pedido pedido = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com o ID: " + id));
    return converterParaDTO(pedido);
    }

    private PedidoResponseDTO converterParaDTO(Pedido pedido) {
        PedidoResponseDTO response = mapper.map(pedido, PedidoResponseDTO.class);
        response.setNomeCliente(pedido.getCliente().getNome());
        response.setNomeRestaurante(pedido.getRestaurante().getNome());
        if (pedido.getItens() != null) {
            response.setItens(pedido.getItens().stream().map(item -> {
                var itemDTO = new com.deliverytech.delivery_api.dto.responses.ItemPedidoResponseDTO();
                itemDTO.setNomeProduto(item.getProduto().getNome());
                itemDTO.setQuantidade(item.getQuantidade());
                itemDTO.setPrecoUnitario(item.getPrecoUnitario());
                itemDTO.setSubtotal(item.getSubtotal());
                return itemDTO;
            }).toList());
        }
        return response;
    }

    public List<PedidoResponseDTO> buscarPorStatus(StatusPedido status){
        return repository.findByStatus(status).stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public List<PedidoResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByDataPedidoBetween(inicio, fim).stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public List<PedidoResponseDTO> buscarPorData(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByDataPedidoBetween(inicio, fim).stream()
                .map(this::converterParaDTO)
                .toList();
    }
    
    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        
        pedido.setStatus(novoStatus);
        return converterParaDTO(repository.save(pedido));
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