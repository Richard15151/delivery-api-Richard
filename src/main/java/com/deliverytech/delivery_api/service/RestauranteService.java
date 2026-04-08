package com.deliverytech.delivery_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.deliverytech.delivery_api.dto.responses.RestauranteResponseDTO;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.dto.requests.RestauranteDTO;
// import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;

import jakarta.transaction.Transactional;

@Service
public class RestauranteService {
    private final RestauranteRepository repository;
    private final ModelMapper mapper;

    public RestauranteService(RestauranteRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    private Restaurante buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));
    }

    @Transactional
    public RestauranteResponseDTO cadastrar(RestauranteDTO dto) {
        if( repository.existsByNome(dto.getNome()) ){
            throw new BusinessException("Restaurante já cadastrado.");
        }
        Restaurante restaurante = mapper.map(dto, Restaurante.class);
        restaurante.setAtivo(true);
        return mapper.map(repository.save(restaurante), RestauranteResponseDTO.class);
    }

    public List<RestauranteResponseDTO> listarAtivos() {
        return repository.findByAtivoTrue().stream()
                .map(r -> mapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    public RestauranteResponseDTO buscarPorId(Long id) {
        Restaurante restaurante = buscarEntidade(id);
        return mapper.map(restaurante, RestauranteResponseDTO.class);
    }
    
    public List<RestauranteResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(restaurante -> mapper.map(restaurante, RestauranteResponseDTO.class))
                .toList();
    }

    public List<RestauranteResponseDTO> buscarPorCategoria(String categoria) {
        return repository.findByCategoriaContainingIgnoreCase(categoria)
                .stream()
                .map(restaurante -> mapper.map(restaurante, RestauranteResponseDTO.class))
                .toList();
    }

    public List<RestauranteResponseDTO> listarRanking() {
        return repository.findByAtivoTrueOrderByAvaliacaoDesc()
                .stream()
                .map(restaurante -> mapper.map(restaurante, RestauranteResponseDTO.class))
                .toList();
    }

    @Transactional
    public void alternarStatus(Long id) {
        Restaurante restaurante = buscarEntidade(id);
        restaurante.setAtivo(!restaurante.isAtivo());
        repository.save(restaurante);
    }

    public RestauranteResponseDTO atualizar(Long id, RestauranteDTO dados) {
        Restaurante restaurante = buscarEntidade(id);
        restaurante.setNome(dados.getNome());
        restaurante.setCategoria(dados.getCategoria());
        restaurante.setEndereco(dados.getEndereco());
        restaurante.setTelefone(dados.getTelefone());
        // restaurante.setTaxaEntrega(dados.getTaxaEntrega());

        return mapper.map(repository.save(restaurante), RestauranteResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Restaurante não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }

}
