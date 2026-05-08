package com.deliverytech.delivery_api.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;

@Component
public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        String correlationId = UUID.randomUUID().toString();

        MDC.put("correlationId", correlationId);

        try{
            chain.doFilter(request, response);
        }finally{
            MDC.clear();
        }
    }
}