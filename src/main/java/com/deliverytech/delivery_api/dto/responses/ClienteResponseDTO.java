package com.deliverytech.delivery_api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ClienteResponse", description = "Objeto que representa os dados de saída de um cliente cadastrado.")
public class ClienteResponseDTO {

    @Schema(description = "Identificador único do cliente no banco de dados", example = "1")
    private Long id;

    @Schema(description = "Nome completo do cliente", example = "Richard Oliveira")
    private String nome;

    @Schema(description = "E-mail cadastrado do cliente", example = "elaine@gmail.com")
    private String email;

    @Schema(description = "Telefone de contato", example = "(11) 98765-4321")
    private String telefone;

    @Schema(description = "Endereço completo de entrega", example = "Rua das Flores, 123 - Centro")
    private String endereco;

    @Schema(description = "Indica se o cliente está com o cadastro ativo no sistema", example = "true")
    private Boolean ativo;

    public Boolean isAtivo(){
        return ativo;
    }
}