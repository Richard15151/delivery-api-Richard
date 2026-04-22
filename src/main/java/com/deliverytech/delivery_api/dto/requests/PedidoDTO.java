package com.deliverytech.delivery_api.dto.requests;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "PedidoRequest", description = "Dados para criação de um novo pedido.")
public class PedidoDTO {

    @Schema(
        description = "Endereço residencial para entrega", 
        example = "Rua das Flores, 123 - Centro", 
        minLength = 5, 
        maxLength = 255
    )
    @Size(min = 5, message = "Endereço deve ter no mínimo 5 caracteres")
    private String enderecoEntrega;

    @Schema(description = "ID do cliente que está realizando o pedido.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @Schema(description = "ID do restaurante onde o pedido está sendo feito.", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;

    @Schema(description = "Lista de produtos e quantidades.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "A lista de itens não pode ser nula")
    @Size(min = 1, message = "O pedido deve ter pelo menos um item")
    private List<ItemPedidoDTO> itens;
 

}