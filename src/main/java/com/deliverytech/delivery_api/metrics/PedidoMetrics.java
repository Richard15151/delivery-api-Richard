package com.deliverytech.delivery_api.metrics;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class PedidoMetrics {

    private final Counter pedidosCriados;
    private final Timer tempoProcessamento;

    private final Counter pedidosAprovados;
    private final Counter pedidosCancelados;

    public PedidoMetrics(MeterRegistry registry) {
        this.pedidosCriados = registry.counter("pedidos_criados_total");
        this.tempoProcessamento = registry.timer("tempo_processamento_pedido");

        this.pedidosAprovados = registry.counter("pedidos_aprovados_total");
        this.pedidosCancelados = registry.counter("pedidos_cancelados_total");
    }

    public void incrementarPedidos(){
        pedidosCriados.increment();
    }

    public void medirTempo(Runnable runnable){
        tempoProcessamento.record(runnable);
    }

    public void pedidoAprovado(){
        pedidosAprovados.increment();
    }

    public void pedidoCancelado(){
        pedidosCancelados.increment();
    }
}