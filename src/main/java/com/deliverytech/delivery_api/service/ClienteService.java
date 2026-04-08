package com.deliverytech.delivery_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import com.deliverytech.delivery_api.dto.requests.ClienteDTO;
import com.deliverytech.delivery_api.dto.responses.ClienteResponseDTO;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Cliente;
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

    public Cliente buscarPorId(Long id){
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
    }

    private Cliente buscarEntidade(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteDTO dto){
        if( repository.existsByEmail(dto.getEmail()) ){
            throw new BusinessException("E-mail já cadastrado.");
        }
        Cliente cliente = mapper.map(dto, Cliente.class);
        cliente.setAtivo(true);
        Cliente salvo = repository.save(cliente);
        return mapper.map(salvo, ClienteResponseDTO.class);
    }

    public List<ClienteResponseDTO> listarAtivos(){
        return repository.findByAtivoTrue()
            .stream()
            .map(c -> mapper.map(c, ClienteResponseDTO.class))
            .toList();
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
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }
}