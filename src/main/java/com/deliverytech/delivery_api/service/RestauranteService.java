package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dto.responses.RestauranteResponseDTO;
import com.deliverytech.delivery_api.enums.CategoriaRestaurante;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.dto.requests.RestauranteDTO;
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
        if (repository.existsByNome(dto.getNome())) {
            throw new BusinessException("Restaurante com esse nome já cadastrado.");
        }

        Restaurante r = mapper.map(dto, Restaurante.class);
        r.setAtivo(true);
        r.setAvaliacao(BigDecimal.ZERO);
        
        Restaurante salvo = repository.save(r);
        return mapper.map(salvo, RestauranteResponseDTO.class);
    }

    public Page<RestauranteResponseDTO> listarAtivos(Pageable pageable) {
        return repository.findByAtivoTrue(pageable)
                .map(r -> mapper.map(r, RestauranteResponseDTO.class));
    }

    public RestauranteResponseDTO buscarPorId(Long id) {
        Restaurante r = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));
        return mapper.map(r, RestauranteResponseDTO.class);
    }

    
    public List<RestauranteResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(restaurante -> mapper.map(restaurante, RestauranteResponseDTO.class))
                .toList();
    }

    public Page<RestauranteResponseDTO> buscarPorCategoria(String categoria, Pageable pageable) {
        CategoriaRestaurante categoriaEnum;

        try{
            categoriaEnum = CategoriaRestaurante.valueOf(categoria.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new BusinessException("Categoria inválida.");
        }

        return repository.findByCategoriaAndAtivoTrue(categoriaEnum, pageable)
                .map(r -> mapper.map(r, RestauranteResponseDTO.class));
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

    @Transactional
    public RestauranteResponseDTO toggle(Long id) {
        Restaurante restaurante = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        restaurante.setAtivo(!restaurante.isAtivo());
        return mapper.map(restaurante, RestauranteResponseDTO.class);
    }

}
