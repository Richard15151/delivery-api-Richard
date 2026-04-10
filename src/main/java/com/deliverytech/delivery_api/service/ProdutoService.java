package com.deliverytech.delivery_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    private final ProdutoRepository repository;
    private final RestauranteRepository restauranteRepository;
    private final ModelMapper mapper;

    public ProdutoService(ProdutoRepository repository, RestauranteRepository restauranteRepository, ModelMapper mapper) {
        this.repository = repository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
    }

    @Transactional
    public ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto) {
        // Validação: Restaurante deve existir
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        // Validação: Nome único no mesmo restaurante
        if (repository.existsByNomeAndRestauranteId(dto.getNome(), dto.getRestauranteId())) {
            throw new BusinessException("Produto já cadastrado neste restaurante.");
        }

        Produto produto = mapper.map(dto, Produto.class);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        
        return mapper.map(repository.save(produto), ProdutoResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarDisponiveis(){
        return repository.findByDisponivelTrue().stream()
                .map(r -> mapper.map(r, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = buscarEntidade(id);
        return mapper.map(produto, ProdutoResponseDTO.class);
    }

    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId) {
        return repository.findByRestauranteId(restauranteId).stream()
                .map(p -> mapper.map(p, ProdutoResponseDTO.class)).toList();
    }

    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        return repository.findByCategoriaIgnoreCase(categoria).stream()
                .map(p -> mapper.map(p, ProdutoResponseDTO.class)).toList();
    }

    @Transactional
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = buscarEntidade(id);
        produto.setDisponivel(disponivel);
        repository.save(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = buscarEntidade(id);
        
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());

        return mapper.map(repository.save(produto), ProdutoResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }
}
