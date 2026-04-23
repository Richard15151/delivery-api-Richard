package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dto.requests.ItemPedidoDTO;
import com.deliverytech.delivery_api.dto.requests.PedidoDTO;
import com.deliverytech.delivery_api.dto.responses.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.ItemPedidoRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.repository.UsuarioRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final ModelMapper mapper;

    private PedidoResponseDTO toResponseDTO(Pedido pedido){
        return mapper.map(pedido, PedidoResponseDTO.class);
    }
    
    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
            RestauranteRepository restauranteRepository, ItemPedidoRepository itemPedidoRepository, ModelMapper mapper, ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    private PedidoResponseDTO toDTO(Pedido pedido) {
        return mapper.map(pedido, PedidoResponseDTO.class);
    }

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoDTO dto, Usuario usuarioLogado) {

        if (usuarioLogado == null) {
        throw new BusinessException("Usuário não autenticado.");
        }

        Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        Cliente cliente = usuarioAtualizado.getCliente();
        
        if (cliente == null) {
            throw new BusinessException("Usuário logado não possui um perfil de cliente vinculado.");
        }

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(dto.getEnderecoEntrega());

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : dto.getItens()) {

            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

            if (!produto.isDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            BigDecimal subtotal = produto.getPreco()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));

            item.setSubtotal(subtotal);

            pedido.getItens().add(item);
            total = total.add(subtotal);
        }

        pedido.setValorTotal(total);

        return toDTO(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO confirmarPedido(Long pedidoId){
        Pedido pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(() -> new EntityNotFoundException("Pedido não localizado.") );

        if(pedido.getStatus() != StatusPedido.PENDENTE){
            throw new BusinessException("Apenas pedidos PENDENTES podem ser confirmados.");
        }

        pedido.setStatus(StatusPedido.CONFIRMADO);
        return toResponseDTO(pedido);
    }

    public Page<PedidoResponseDTO> listarPorCliente(Long clienteId, Pageable pageable) {
        return pedidoRepository.buscarItensPorClientes(clienteId, pageable)
            .map(this::toResponseDTO);
    }

    private void validarDonoPedido(Pedido pedido, Usuario usuarioLogado) {
        if (!pedido.getCliente().getEmail().equals(usuarioLogado.getEmail())) {
            throw new BusinessException("Você não tem permissão para acessar este pedido.");
        }
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id, Usuario usuarioLogado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (usuarioLogado.getRole().name().equals("CLIENTE")) {
            if (!pedido.getCliente().getUsuario().getId().equals(usuarioLogado.getId())) {
                throw new BusinessException("Acesso negado: Este pedido não pertence a você.");
            }
        }

        return mapper.map(pedido, PedidoResponseDTO.class);
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
        return pedidoRepository.findByStatus(status).stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(inicio, fim);
        
        return pedidos.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public List<PedidoResponseDTO> buscarPorData(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataPedidoBetween(inicio, fim).stream()
                .map(this::converterParaDTO)
                .toList();
    }
    
    @Transactional
    public PedidoResponseDTO atualizarStatus(Long pedidoId){
        Pedido pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(()-> new EntityNotFoundException("Pedido não encontrado."));

        StatusPedido statusAtual = pedido.getStatus();

        switch(statusAtual){
            case CONFIRMADO -> pedido.setStatus(StatusPedido.PREPARANDO);
            case PREPARANDO -> pedido.setStatus(StatusPedido.SAIU_PARA_ENTREGA);
            case SAIU_PARA_ENTREGA -> pedido.setStatus(StatusPedido.ENTREGUE);

            case CANCELADO, ENTREGUE -> 
                throw new BusinessException("Status do Pedido não pode mais ser avançado.");
            default ->
                throw new BusinessException("Status é inválido para avanço.");
        }
        return toResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long pedidoId, Usuario usuarioLogado) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado."));

        validarDonoPedido(pedido, usuarioLogado);

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new BusinessException("Pedido entregue não pode ser cancelado.");
        }

        pedido.setStatus(StatusPedido.CANCELADO);

        return toDTO(pedidoRepository.save(pedido));
    }

    public Page<PedidoResponseDTO> meusPedidos(Usuario usuarioLogado, Pageable pageable) {

        Cliente cliente = clienteRepository.findByEmail(usuarioLogado.getEmail())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado."));

        return pedidoRepository.buscarItensPorClientes(cliente.getId(), pageable)
                .map(this::toDTO);
    }
    
    public BigDecimal obterFaturamentoTotal(LocalDateTime inicio, LocalDateTime fim, Usuario usuarioLogado) {
        Long restauranteId = null;

        if (usuarioLogado.getRole().name().equals("RESTAURANTE")) {
            restauranteId = obterIdRestauranteDoUsuario(usuarioLogado);
        } else if (!usuarioLogado.getRole().name().equals("ADMIN")) {
            throw new BusinessException("Acesso negado aos relatórios.");
        }

        BigDecimal total = pedidoRepository.calcularTotalVendido(inicio, fim, restauranteId);
        return (total != null) ? total : BigDecimal.ZERO;
    }

    public Long contarPedidosPorStatus(StatusPedido status, Usuario usuarioLogado) {
        Long restauranteId = null;
        if (usuarioLogado.getRole().name().equals("RESTAURANTE")) {
            restauranteId = obterIdRestauranteDoUsuario(usuarioLogado);
        }
        return pedidoRepository.countByStatusAndRestaurante(status, restauranteId);
    }

    public List<Object[]> obterRankingProdutos(Usuario usuarioLogado) {
        Long restauranteId = null;
        if (usuarioLogado.getRole().name().equals("RESTAURANTE")) {
            restauranteId = obterIdRestauranteDoUsuario(usuarioLogado);
        }
        return pedidoRepository.buscarProdutosMaisVendidos(restauranteId);
    }

    private Long obterIdRestauranteDoUsuario(Usuario usuario) {
        return restauranteRepository.findByUsuario_Id_Custom(usuario.getId()) 
                .orElseThrow(() -> new BusinessException("Usuário não possui restaurante vinculado."))
                .getId();
    }
}