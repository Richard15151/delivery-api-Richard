package com.deliverytech.delivery_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dto.requests.ProdutoDTO;
import com.deliverytech.delivery_api.dto.responses.ProdutoResponseDTO;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.transaction.annotation.Transactional;

// import jakarta.transaction.Transactional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ModelMapper mapper;

    public ProdutoService(ProdutoRepository produtoRepository, RestauranteRepository restauranteRepository, ModelMapper mapper) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    private ProdutoResponseDTO returnResponseDTO(Produto p) {
    ProdutoResponseDTO dto = mapper.map(p, ProdutoResponseDTO.class);
    if (p.getRestaurante() != null) {
        dto.setRestauranteId(p.getRestaurante().getId());
        dto.setRestauranteNome(p.getRestaurante().getNome());
    }
    return dto;
}

    @Transactional
    public ProdutoResponseDTO cadastrar(
            Long restauranteId,
            ProdutoDTO dto,
            Usuario usuarioLogado) {

        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        boolean isRestaurante = usuarioLogado.getRole().name().equals("RESTAURANTE");
        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");

        if (!isRestaurante && !isAdmin) {
            throw new BusinessException("Acesso negado.");
        }

        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

    
        if (isRestaurante &&
                !restaurante.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você só pode cadastrar produtos no seu restaurante.");
        }

        Produto produto = mapper.map(dto, Produto.class);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);

        return returnResponseDTO(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarDisponiveis(){
        return produtoRepository.findByDisponivelTrue().stream()
                .map(this::returnResponseDTO)
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
    Produto p = produtoRepository.buscarComRestaurante(id) 
            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    
    return returnResponseDTO(p);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId, Pageable pageable) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException("Restaurante não localizado.");
        }
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId, pageable)
                .map(this::returnResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaIgnoreCase(categoria).stream()
                .map(this::returnResponseDTO)   
                .toList();
    }

    @Transactional
    public ProdutoResponseDTO toggleDisponibilidade(Long produtoId, Usuario usuarioLogado) {

        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        boolean isRestaurante = usuarioLogado.getRole().name().equals("RESTAURANTE");
        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");

        if (!isRestaurante && !isAdmin) {
            throw new BusinessException("Acesso negado.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (isRestaurante &&
                !produto.getRestaurante().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não pode alterar produto de outro restaurante.");
        }

        produto.setDisponivel(!produto.isDisponivel());

        return returnResponseDTO(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto, Usuario usuarioLogado) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        if (usuarioLogado.getRole().name().equals("RESTAURANTE") && 
            !produto.getRestaurante().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Acesso negado.");
        }

        mapper.map(dto, produto);
        return mapper.map(produtoRepository.save(produto), ProdutoResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id, Usuario usuarioLogado) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        if (usuarioLogado.getRole().name().equals("RESTAURANTE") && 
            !produto.getRestaurante().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não tem permissão para excluir este produto.");
        }

        produto.setDisponivel(false);
        produtoRepository.save(produto);
    }
}
