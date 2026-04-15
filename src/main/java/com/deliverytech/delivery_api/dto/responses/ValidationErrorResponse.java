package com.deliverytech.delivery_api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.time.LocalDateTime;

@Schema(name = "ValidationErrorResponse", description = "Estrutura detalhada para erros de validação de campos (Status 400).")
public record ValidationErrorResponse(
    
    @Schema(description = "Indica se a operação foi bem-sucedida (sempre false para erros).", example = "false")
    boolean success,

    @Schema(description = "Mensagem genérica resumindo o erro.", example = "Erro de validação nos campos enviados.")
    String message,

    @Schema(description = "Lista contendo cada campo que falhou na validação e sua respectiva causa.")
    List<FieldInfo> errors,

    @Schema(description = "Data e hora exata em que o erro ocorreu.")
    LocalDateTime timestamp
) {

    @Schema(name = "FieldInfo", description = "Informação detalhada sobre o erro em um campo específico.")
    public record FieldInfo(
        @Schema(description = "Nome do campo que gerou o erro.", example = "email")
        String field, 
        
        @Schema(description = "Mensagem de erro específica da validação.", example = "Formato de e-mail inválido.")
        String message
    ) {}
}