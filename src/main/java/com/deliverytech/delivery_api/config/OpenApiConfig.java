package com.deliverytech.delivery_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;


@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
            .info( new Info()
                .title("Delivery API")
                .description("API para gerenciamento")
                .version("1.0")
                .contact(new Contact().name("Suporte").email("suporte@email.com"))
            ).servers(Arrays.asList(new Server().url("http://localhost:8081").description("Servidor local")));
    }
}
