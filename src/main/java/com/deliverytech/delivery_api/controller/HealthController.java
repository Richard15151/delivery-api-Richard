package com.deliverytech.delivery_api.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Monitoramento", description = "Endpoints para verificação de saúde e informações do sistema")
public class HealthController {

    @Operation(
        summary = "Verificar integridade do sistema (Health Check)", 
        description = "Retorna o status atual da aplicação, timestamp e versão do Java em execução."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Sistema operando normalmente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"status\": \"UP\", \"service\": \"Delivery Api\", \"javaVersion\": \"21\"}")
            )
        )
    })
    @GetMapping("/health")
    public Map<String, String> health(){
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "service", "Delivery Api",
            "javaVersion", System.getProperty("java.version")
        );   
    }

    @Operation(
        summary = "Informações da aplicação", 
        description = "Retorna detalhes sobre a versão da API, desenvolvedor e tecnologias utilizadas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações recuperadas com sucesso")
    })
    @GetMapping("/info")
    public AppInfo info(){
        return new AppInfo(
            "Delivery Tech Api",
            "1.0.0",
            "Richard Oliveira",
            "JDK 21",
            "Spring Boot"
        );
    }

    public record AppInfo(
        String application,
        String version,
        String developer,
        String javaVersion,
        String framework
    ){}
}