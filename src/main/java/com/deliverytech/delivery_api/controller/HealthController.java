package com.deliverytech.delivery_api.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.metrics.PedidoMetrics;

@RestController
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    
    private final PedidoMetrics pedidoMetrics;


    public HealthController(PedidoMetrics pedidoMetrics) {
        this.pedidoMetrics = pedidoMetrics;
    }
            /* custmon-health */
    @GetMapping("/custom-health")
    public Map<String, String> health(){
        log.info("Endpoint {} chamado às {}", "/custom-health", LocalDateTime.now());
        log.info("Teste MDC funcionando.");
        
        pedidoMetrics.incrementarPedidos();
        pedidoMetrics.pedidoAprovado();
        pedidoMetrics.pedidoCancelado();


        pedidoMetrics.medirTempo(()->{
            try{
                Thread.sleep(200);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });

        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "service", "Delivery Api",
            "javaVersion", System.getProperty("java.version")
        );   
    }

    @GetMapping("/custom-info")
    public AppInfo info(){
        return new AppInfo(
            "Delivery Tech Api",
            "1.0.0",
            "Elaine Soares",
            "JDK 21",
            "Spring Boot"
        );
    }

    @GetMapping("/api/teste")
    public String teste() {
        return "ok";
    }

    public record AppInfo(
        String application,
        String version,
        String developer,
        String javaVersion,
        String framework
    ){}





}

