package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
import com.deliverytech.delivery_api.model.Usuario;
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
    public RestauranteResponseDTO cadastrar(RestauranteDTO dto, Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        if (repository.existsByUsuario_Id(usuarioLogado.getId())) {
            throw new BusinessException("Acesso negado: Este usuário já possui um restaurante cadastrado.");
        }

        if (repository.existsByNome(dto.getNome())) {
            throw new BusinessException("Já existe um restaurante cadastrado com este nome.");
        }

        try {
            CategoriaRestaurante categoriaEnum =
                    CategoriaRestaurante.valueOf(dto.getCategoria().toUpperCase());

            Restaurante r = mapper.map(dto, Restaurante.class);
            r.setUsuario(usuarioLogado); 
            r.setCategoria(categoriaEnum);
            r.setAtivo(true);
            r.setAvaliacao(BigDecimal.ZERO);
            r.setCep(dto.getCep());

            return mapper.map(repository.save(r), RestauranteResponseDTO.class);
            
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Categoria inválida: " + dto.getCategoria());
        }
    }

    public Page<RestauranteResponseDTO> listarAtivos(Pageable pageable) {
        return repository.findByAtivoTrueOrderByAvaliacaoDesc(pageable)
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

    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cepCliente) {
        Restaurante restaurante = repository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        if (cepCliente == null || restaurante.getCep() == null) {
            return restaurante.getTaxaEntrega();
        }

        if (cepCliente.substring(0, 3).equals(restaurante.getCep().substring(0, 3))) {
            return restaurante.getTaxaEntrega().multiply(new BigDecimal("0.5"));
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
    public RestauranteResponseDTO atualizar(Long id, RestauranteDTO dto, Usuario usuarioLogado) {
        Restaurante restaurante = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        if (usuarioLogado.getRole().name().equals("RESTAURANTE") && 
            !restaurante.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você só pode atualizar o seu próprio restaurante.");
        }

        mapper.map(dto, restaurante);
        restaurante.setCep(dto.getCep());
        
        return mapper.map(repository.save(restaurante), RestauranteResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        Restaurante restaurante = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));
        
        restaurante.setAtivo(false);
        repository.save(restaurante);
    }

    @Transactional
    public RestauranteResponseDTO toggle(Long id, Usuario usuarioLogado) {

        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        boolean isRestaurante = usuarioLogado.getRole().name().equals("RESTAURANTE");
        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");

        if (!isRestaurante && !isAdmin) {
            throw new BusinessException("Apenas ADMIN ou RESTAURANTE podem alterar restaurante.");
        }

        Restaurante restaurante = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));
        if (isRestaurante) {
            if (!restaurante.getUsuario().getId().equals(usuarioLogado.getId())) {
                throw new BusinessException("Você só pode alterar seu próprio restaurante.");
            }
        }

        restaurante.setAtivo(!restaurante.isAtivo());

        Restaurante salvo = repository.save(restaurante);

        return mapper.map(salvo, RestauranteResponseDTO.class);
    }

    public List<RestauranteResponseDTO> buscarProximos(String cepCliente) {
        if (cepCliente == null || cepCliente.length() < 5) {
            throw new BusinessException("CEP inválido para busca de proximidade.");
        }
        
        String prefixo = cepCliente.substring(0, 3);
        List<Restaurante> restaurantes = repository.findByCepStartingWith(prefixo);
        
        return restaurantes.stream()
                .map(r -> mapper.map(r, RestauranteResponseDTO.class))
                .toList();
    }
}
