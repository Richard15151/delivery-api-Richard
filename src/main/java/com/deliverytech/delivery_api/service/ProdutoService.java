package com.deliverytech.delivery_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.repository.ProdutoRepository;

@Service
public class ProdutoService {
    private ProdutoRepository repository;

    public ProdutoService (ProdutoRepository repository){
        this.repository = repository;
    }

    public Produto cadastrar(Produto produto){
        String nome = produto.getNome();
        Long restauranteId = produto.getRestaurante().getId();
        if (repository.existsByNomeAndRestauranteId(nome, restauranteId)) {
        throw new IllegalArgumentException("Produto já cadastrado em seu cardápio.");
    }
    produto.setDisponivel(true);
    return repository.save(produto);
    }

    public List<Produto> listarDisponiveis(){
        return repository.findByDisponivelTrue();
    }

    public Produto buscarPorId(Long id){
        return repository.findById(id).orElseThrow(()-> new IllegalArgumentException("Produto não encontrado."));
    }

    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        return repository.findByRestauranteId(restauranteId);
    }

    public List<Produto> buscarPorCategoria(String categoria) {
        return repository.findByCategoriaIgnoreCase(categoria);
    }

    public void alternarStatus(Long id) {
        Produto produto = buscarPorId(id);
        produto.setDisponivel(!produto.getDisponivel());
        repository.save(produto);
    }

    public Produto atualizar(Long id, Produto dados){
        Produto produto = buscarPorId(id);
        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());
        produto.setCategoria(dados.getCategoria());
        produto.setPreco(dados.getPreco());
        return repository.save(produto);
    }

    public void deletar(Long id){
        if (!repository.existsById(id)){
            throw new IllegalArgumentException("Produto não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }
}
