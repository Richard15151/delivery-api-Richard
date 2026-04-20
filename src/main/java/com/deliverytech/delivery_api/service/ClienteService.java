package com.deliverytech.delivery_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dto.requests.ClienteDTO;
import com.deliverytech.delivery_api.dto.responses.ClienteResponseDTO;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.repository.ClienteRepository;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {
    private final ClienteRepository repository;
    private final ModelMapper mapper;

    public ClienteService (ClienteRepository repository, ModelMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    public Cliente buscarPorEmail(String email){
        return repository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
    }

    public ClienteResponseDTO buscarPorId(Long id){
        Cliente cliente =  repository.findById(id)
        .orElseThrow(()-> new EntityNotFoundException("Cliente não encontrado."));

        return mapper.map(cliente, ClienteResponseDTO.class);
    }

    private Cliente buscarEntidade(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
    }

     @Transactional
    public ClienteResponseDTO cadastrar(ClienteDTO dto, Usuario usuarioLogado) {

        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado.");
        }

        if (!usuarioLogado.getRole().name().equals("CLIENTE")
            && !usuarioLogado.getRole().name().equals("ADMIN")) {
            throw new BusinessException("Apenas CLIENTE ou ADMIN podem criar perfil de cliente.");
        }

        if (repository.existsByUsuario_Id(usuarioLogado.getId())) {
            throw new BusinessException("Cliente já cadastrado para este usuário.");
        }

        Cliente cliente = mapper.map(dto, Cliente.class);

        cliente.setUsuario(usuarioLogado);
        cliente.setEmail(usuarioLogado.getEmail());
        cliente.setAtivo(true);

        Cliente salvo = repository.save(cliente);

        return mapper.map(salvo, ClienteResponseDTO.class);
    }

    @Transactional
    public ClienteResponseDTO alternarStatus(Long id) {
        Cliente cliente = buscarEntidade(id); 
        cliente.setAtivo(!cliente.getAtivo()); 
        Cliente clienteSalvo = repository.save(cliente);
        return mapper.map(clienteSalvo, ClienteResponseDTO.class);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteDTO dados) {
        Cliente cliente = buscarEntidade(id);
        cliente.setNome(dados.getNome());
        cliente.setEmail(dados.getEmail());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEndereco(dados.getEndereco());

        Cliente clienteAtualizado = repository.save(cliente);
        return mapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id){
        if (!repository.existsByUsuario_Id(id)) {
            throw new EntityNotFoundException("Cliente não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }

    public Page<ClienteResponseDTO> listarAtivos(Pageable pageable) {
    Page<Cliente> clientes = repository.findByAtivoTrue(pageable);
    return clientes.map(cliente -> mapper.map(cliente, ClienteResponseDTO.class));
    }
}