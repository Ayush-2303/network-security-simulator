package com.network.slicing.network_security_simulator.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/** Protects state-changing endpoints when APP_API_KEY is configured. */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    private final String apiKey;

    public ApiKeyFilter(@Value("${app.api-key:}") String apiKey) { this.apiKey = apiKey; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        boolean changesState = !"GET".equalsIgnoreCase(request.getMethod()) && !"OPTIONS".equalsIgnoreCase(request.getMethod());
        if (changesState && !apiKey.isBlank() && !apiKey.equals(request.getHeader("X-API-Key"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "A valid X-API-Key is required");
            return;
        }
        chain.doFilter(request, response);
    }
}
