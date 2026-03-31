package com.deliverytech.delivery_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;
import jakarta.persistence.CascadeType; 
import jakarta.persistence.FetchType; 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="pedido")
public class Pedido {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private LocalDateTime dataPedido;
    private String enderecoEntrega;
    private String numeroPedido;
    private BigDecimal taxaEntrega;
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private StatusPedido status;
    private BigDecimal valorTotal;

    @ManyToOne 
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne 
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER) 
    private List<ItemPedido> itens;
}
