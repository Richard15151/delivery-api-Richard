package com.deliverytech.delivery_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
            .info(new Info()
                .title("DeliveryTech API")
                .description("Plataforma robusta de delivery para integração de parceiros e apps mobile.")
                .version("2.0")
                .contact(new Contact().name("Tech Support").email("dev@deliverytech.com"))
            )
            .addServersItem(new Server().url("http://localhost:8081").description("Dev Server"));
    }
    
}