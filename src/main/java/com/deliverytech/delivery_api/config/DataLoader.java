package com.deliverytech.delivery_api.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.deliverytech.delivery_api.enums.CategoriaRestaurante;
import com.deliverytech.delivery_api.enums.Role;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.ItemPedidoRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.repository.UsuarioRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner iniciarDados(
        UsuarioRepository usuarioRepository,
        ClienteRepository clienteRepository,
        RestauranteRepository restauranteRepository,
        ProdutoRepository produtoRepository,
        PedidoRepository pedidoRepository,
        ItemPedidoRepository itemPedidoRepository,
        PasswordEncoder passwordEncoder 
    ){
        return args -> {
            System.out.println("===== Inserindo Clientes e Usuarios ======");
            
            Usuario u1 = criarUsuario(usuarioRepository, passwordEncoder, "joao@gmail.com", Role.CLIENTE);
            Cliente c1 = new Cliente();
            c1.setNome("João Freitas");
            c1.setEmail("joao@gmail.com");
            c1.setTelefone("119999-8888");
            c1.setEndereco("av 1, 111");
            c1.setCep("01001000");
            c1.setUsuario(u1);
            c1.setAtivo(true);

            Usuario u2 = criarUsuario(usuarioRepository, passwordEncoder, "mariana@gmail.com", Role.CLIENTE);
            Cliente c2 = new Cliente();
            c2.setNome("Mariana Freitas");
            c2.setEmail("mariana@gmail.com");
            c2.setTelefone("119999-7777");
            c2.setEndereco("av 2, 222");
            c2.setCep("01001001");
            c2.setUsuario(u2);
            c2.setAtivo(true);

            Usuario u3 = criarUsuario(usuarioRepository, passwordEncoder, "joanna@gmail.com", Role.CLIENTE);
            Cliente c3 = new Cliente();
            c3.setNome("Joanna Silva");
            c3.setEmail("joanna@gmail.com");
            c3.setTelefone("119999-7777");
            c3.setEndereco("av 3, 333");
            c3.setCep("20040002");
            c3.setUsuario(u3);
            c3.setAtivo(true);

            clienteRepository.saveAll(List.of(c1, c2, c3));

            System.out.println("===== Inserindo Restaurantes e Usuarios ======");

            Usuario uRest1 = criarUsuario(usuarioRepository, passwordEncoder, "pizzatop@email.com", Role.RESTAURANTE);
            Restaurante r1 = new Restaurante();
            r1.setNome("Pizza Top");
            r1.setCategoria(CategoriaRestaurante.PIZZARIA);
            r1.setEndereco("Rua um, 111");
            r1.setCep("01001000");
            r1.setTelefone("11 9999-1111");
            r1.setTaxaEntrega(new BigDecimal("7.00"));
            r1.setAvaliacao(new BigDecimal("4.5"));
            r1.setUsuario(uRest1);
            r1.setAtivo(true);

            Usuario uRest2 = criarUsuario(usuarioRepository, passwordEncoder, "burgerhouse@email.com", Role.RESTAURANTE);
            Restaurante r2 = new Restaurante();
            r2.setNome("Burger House");
            r2.setCategoria(CategoriaRestaurante.HAMBURGUERIA);
            r2.setEndereco("Rua dois, 222");
            r2.setCep("01005000");
            r2.setTelefone("11 9999-2222");
            r2.setTaxaEntrega(new BigDecimal("12.00"));
            r2.setAvaliacao(new BigDecimal("4.2"));
            r2.setUsuario(uRest2);
            r2.setAtivo(true);

            restauranteRepository.saveAll(List.of(r1, r2));

            System.out.println("===== Inserindo Produtos ======");

            Produto p1 = new Produto();
            p1.setNome("Pizza de calabresa");
            p1.setDescricao("Pizza de calabresa com queijo");
            p1.setPreco(new BigDecimal("40.00"));
            p1.setCategoria("Pizza");
            p1.setDisponivel(true);
            p1.setRestaurante(r1);

            Produto p4 = new Produto();
            p4.setNome("X-Burger");
            p4.setDescricao("Hambúrguer tradicional");
            p4.setPreco(new BigDecimal("25.00"));
            p4.setCategoria("Lanche");
            p4.setDisponivel(true);
            p4.setRestaurante(r2);

            produtoRepository.saveAll(List.of(p1, p4));

            System.out.println("===== Inserindo Pedidos ======");
            Pedido pedido1 = new Pedido();
            pedido1.setCliente(c1);
            pedido1.setEnderecoEntrega("av 1, 111");
            pedido1.setStatus(StatusPedido.PENDENTE);
            pedido1.setTaxaEntrega(r1.getTaxaEntrega());
            pedido1.setValorTotal(BigDecimal.ZERO);
            pedido1.setRestaurante(r1);

            pedidoRepository.save(pedido1);

            System.out.println("===== Inserindo ItensPedido ======");
            ItemPedido i1 = new ItemPedido();
            i1.setPedido(pedido1); 
            i1.setProduto(p1);
            i1.setPrecoUnitario(p1.getPreco());
            i1.setQuantidade(2);
            i1.setSubtotal(i1.getPrecoUnitario().multiply(BigDecimal.valueOf(i1.getQuantidade())));

            itemPedidoRepository.save(i1);

            pedido1.setValorTotal(i1.getSubtotal().add(pedido1.getTaxaEntrega()));
            pedidoRepository.save(pedido1);

            System.out.println("===== Inserindo Administrador ======");

            Usuario admin = criarUsuario(usuarioRepository, passwordEncoder, "admin@delivery.com", Role.ADMIN);

            System.out.println("===== Carga de dados finalizada com sucesso! =====");
        };
    }

    private Usuario criarUsuario(UsuarioRepository repo, PasswordEncoder encoder, String email, Role role) {
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setSenha(encoder.encode("123456"));
        u.setAtivo(true);
        u.setRole(role);
        return repo.save(u);
    }
}