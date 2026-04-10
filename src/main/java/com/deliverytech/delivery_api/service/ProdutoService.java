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

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
    }

    private ProdutoResponseDTO returnResponseDTO(Produto p) {
        ProdutoResponseDTO dto = mapper.map(p, ProdutoResponseDTO.class);
        if (p.getRestaurante() != null) {
            dto.setRestauranteId(p.getRestaurante().getId());
        }
        return dto;
    }

    @Transactional
    public ProdutoResponseDTO cadastrar(Long restauranteId, ProdutoDTO produto) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não localizado."));
        
        if (!restaurante.isAtivo()) {
            throw new BusinessException("Restaurante inativo. Não é possível cadastrar produtos.");
        }

        Produto novoProduto = mapper.map(produto, Produto.class);
        novoProduto.setDisponivel(true);
        novoProduto.setRestaurante(restaurante);
        
        return returnResponseDTO(produtoRepository.save(novoProduto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarDisponiveis(){
        return produtoRepository.findByDisponivelTrue().stream()
                .map(r -> mapper.map(r, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto p = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        return returnResponseDTO(p);
    }

    public Page<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId, Pageable pageable) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException("Restaurante não localizado.");
        }
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId, pageable)
                .map(this::returnResponseDTO);
    }

    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaIgnoreCase(categoria).stream()
                .map(p -> mapper.map(p, ProdutoResponseDTO.class)).toList();
    }

    @Transactional
    public ProdutoResponseDTO toggleDisponibilidade(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produto.setDisponivel(!produto.isDisponivel());
        return returnResponseDTO(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = buscarEntidade(id);
        
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());

        return mapper.map(produtoRepository.save(produto), ProdutoResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado para exclusão.");
        }
        produtoRepository.deleteById(id);
    }
}
