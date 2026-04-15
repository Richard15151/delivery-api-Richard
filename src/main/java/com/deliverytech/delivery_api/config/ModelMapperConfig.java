package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.dto.responses.ProdutoResponseDTO;
import com.deliverytech.delivery_api.model.Produto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Produto.class, ProdutoResponseDTO.class).addMappings(mapper -> {
            mapper.skip(ProdutoResponseDTO::setRestauranteId);
            mapper.skip(ProdutoResponseDTO::setRestauranteNome);
        });

        return modelMapper;
    }
}