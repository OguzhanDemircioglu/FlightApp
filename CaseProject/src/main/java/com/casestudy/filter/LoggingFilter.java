package com.casestudy.filter;

import com.casestudy.entity.ServiceLog;
import com.casestudy.repository.ServiceLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private final ServiceLogRepository logRepository;

    public LoggingFilter(ServiceLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Sadece /api/ endpointlerini logla
            if (request.getRequestURI().startsWith("/api/")) {
                try {
                    ServiceLog log = new ServiceLog();
                    log.setTimestamp(LocalDateTime.now());
                    log.setServiceName(resolveServiceName(request.getRequestURI()));
                    log.setEndpointPath(request.getRequestURI());
                    log.setHttpMethod(request.getMethod());
                    log.setRequestBody(
                            new String(wrappedRequest.getContentAsByteArray(),
                                    StandardCharsets.UTF_8));
                    log.setResponseBody(
                            new String(wrappedResponse.getContentAsByteArray(),
                                    StandardCharsets.UTF_8));
                    log.setHttpStatus(wrappedResponse.getStatus());
                    log.setDurationMs(duration);

                    logRepository.save(log);
                } catch (Exception e) {
                    logger.error("Failed to save service log", e);
                }
            }

            // Response body'yi istemciye geri yaz
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String resolveServiceName(String uri) {
        if (uri.contains("/cheapest")) return "searchCheapestFlights";
        if (uri.contains("/search")) return "searchAllFlights";
        return "unknown";
    }
}
