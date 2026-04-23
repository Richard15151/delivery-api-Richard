package com.deliverytech.delivery_api.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dto.requests.ClienteDTO;
import com.deliverytech.delivery_api.dto.responses.ClienteResponseDTO;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.repository.ClienteRepository;

@Service
public class ClienteService {
    private final ClienteRepository repository;
    private final ModelMapper mapper;

    public ClienteService(ClienteRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        return mapper.map(cliente, ClienteResponseDTO.class);
    }

    private Cliente buscarEntidade(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteDTO dto, Usuario usuarioLogado) {
        if (repository.existsByUsuario_Id(usuarioLogado.getId())) {
            throw new BusinessException("Cliente já cadastrado para este usuário.");
        }

        Cliente cliente = mapper.map(dto, Cliente.class);
        cliente.setUsuario(usuarioLogado);
        cliente.setEmail(usuarioLogado.getEmail());
        cliente.setAtivo(true);

        return mapper.map(repository.save(cliente), ClienteResponseDTO.class);
    }

    @Transactional
    public ClienteResponseDTO alternarStatus(Long id) {
        Cliente cliente = buscarEntidade(id); 
        cliente.setAtivo(!cliente.getAtivo()); 
        Cliente clienteSalvo = repository.save(cliente);
        return mapper.map(clienteSalvo, ClienteResponseDTO.class);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteDTO dados, Usuario usuarioLogado) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        // Trava de segurança: Cliente só edita a si mesmo
        if (usuarioLogado.getRole().name().equals("CLIENTE") && !cliente.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não tem permissão para atualizar este perfil.");
        }

        cliente.setNome(dados.getNome());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEndereco(dados.getEndereco());
        cliente.setCep(dados.getCep()); // Atualizando o novo campo CEP

        return mapper.map(repository.save(cliente), ClienteResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        
        // SOFT DELETE: Resolve o Erro 500 de integridade referencial
        cliente.setAtivo(false);
        repository.save(cliente);
    }

    public Page<ClienteResponseDTO> listarAtivos(Pageable pageable) {
        return repository.findByAtivoTrue(pageable)
                .map(cliente -> mapper.map(cliente, ClienteResponseDTO.class));
    }
}