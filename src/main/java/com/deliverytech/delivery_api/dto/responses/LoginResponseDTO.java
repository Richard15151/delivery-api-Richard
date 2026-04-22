package com.deliverytech.delivery_api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LoginResponse", description = "Objeto que representa os dados de saída de um login cadastrado.")
public class LoginResponseDTO {

    @Schema(description = "Token único do usuário para sessão", example = "hd823gf273fg13ef81egf91ge98gff")
    private String token;
}