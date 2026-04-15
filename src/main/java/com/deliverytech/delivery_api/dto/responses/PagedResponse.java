package com.deliverytech.delivery_api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(name = "PagedResponse", description = "Estrutura padrão para retornos paginados da API.")
public record PagedResponse<T>(
    
    @Schema(description = "Lista contendo os itens da página atual.")
    List<T> content,

    @Schema(description = "Número da página atual (inicia em 0).", example = "0")
    int page,

    @Schema(description = "Quantidade de itens solicitados por página.", example = "10")
    int size,

    @Schema(description = "Total de elementos existentes no banco de dados.", example = "50")
    long totalElements,

    @Schema(description = "Total de páginas disponíveis para navegação.", example = "5")
    int totalPages,

    @Schema(description = "Indica se esta é a última página disponível.", example = "false")
    boolean last
) {
    public PagedResponse(Page<T> page){
        this(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}