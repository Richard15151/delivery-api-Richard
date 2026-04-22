package com.deliverytech.delivery_api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Representa o corpo padronizado de erro da API")
public record ErrorResponse(
    
    @Schema(description = "Indica se a operação foi bem-sucedida", example = "false")
    boolean success,

    @Schema(description = "Código técnico do erro", example = "ENTITY_NOT_FOUND")
    String code,

    @Schema(description = "Mensagem amigável sobre o erro", example = "Restaurante não encontrado")
    String message,

    @Schema(description = "Detalhes técnicos adicionais (opcional)", example = "Nenhum restaurante encontrado com ID: 999")
    String details,

    @Schema(description = "Data e hora do erro")
    LocalDateTime timestamp
) {
    public ErrorResponse(String code, String message, String details) {
        this(false, code, message, details, LocalDateTime.now());
    }
}