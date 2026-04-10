package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.deliverytech.delivery_api.dto.responses.RestauranteResponseDTO;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
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
    public RestauranteResponseDTO cadastrarRestaurante(RestauranteDTO dto) {
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
        return mapper.map(buscarEntidade(id), RestauranteResponseDTO.class);
    }
    
    public List<RestauranteResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(restaurante -> mapper.map(restaurante, RestauranteResponseDTO.class))
                .toList();
    }

    public List<RestauranteResponseDTO> buscarPorCategoria(String categoria) {
        return repository.findByCategoriaContainingIgnoreCase(categoria).stream()
                .map(r -> mapper.map(r, RestauranteResponseDTO.class)).toList();
    }

    public List<RestauranteResponseDTO> listarRanking() {
        return repository.findByAtivoTrueOrderByAvaliacaoDesc()
                .stream()
                .map(restaurante -> mapper.map(restaurante, RestauranteResponseDTO.class))
                .toList();
    }

    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = buscarEntidade(restauranteId);
        if (cep.startsWith("0")) {
            return BigDecimal.ZERO;
        }
        return restaurante.getTaxaEntrega();
    }

    @Transactional
    public void alternarStatus(Long id) {
        Restaurante restaurante = buscarEntidade(id);
        restaurante.setAtivo(!restaurante.isAtivo());
        repository.save(restaurante);
    }

    @Transactional
    public RestauranteResponseDTO atualizar(Long id, RestauranteDTO dto) {
        Restaurante restaurante = buscarEntidade(id);
        mapper.map(dto, restaurante); // O ModelMapper copia os dados do DTO para a Entidade existente
        return mapper.map(repository.save(restaurante), RestauranteResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Restaurante não encontrado.");
        }
        repository.deleteById(id);
    }

}
