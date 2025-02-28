package com.audition.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        logger.info("Request URI: {}", request.getURI());
        logger.info("Request Method: {}", request.getMethod());
        logger.info("Request Headers: {}", request.getHeaders());
        logger.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        InputStream responseBodyStream = response.getBody();
        String responseBody = IOUtils.toString(responseBodyStream, StandardCharsets.UTF_8);

        logger.info("Response Status Code: {}", response.getStatusCode());
        logger.info("Response Headers: {}", response.getHeaders());
        logger.info("Response Body: {}", responseBody);
    }
}
