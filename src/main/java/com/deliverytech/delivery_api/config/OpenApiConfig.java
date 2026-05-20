package com.deliverytech.delivery_api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()

            .components(
                new Components()
                .addSecuritySchemes("bearerAuth", 
                    new SecurityScheme()
                    .name("Authorization")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                )
            )

            .addSecurityItem(
                new SecurityRequirement()
                .addList("bearerAuth")
            )

            .info( 
                new Info()
                .title("Delivery API")
                .description("API REST desenvolvida em Java com Spring Boot 3 para o gerenciamento completo de um ecossistema de delivery de comida. A aplicação implementa uma arquitetura robusta em camadas, integrando autenticação e autorização via segurança JWT (com perfis distintos para Clientes, Restaurantes e Administradores), persistência de dados com Spring Data JPA, regras de negócio para cálculo logístico baseado em CEP, relatórios analíticos de faturamento e alta observabilidade através do monitoramento em tempo real com Actuator e Prometheus.")
                .version("1.0")
                .contact(new Contact().name("Suporte").email("suporte@exemplo.com"))
            )
            
            .servers(List.of(
                new Server()
                .url("http://localhost:8081")
                .description("Servidor local")
            ));   
    }
    
}