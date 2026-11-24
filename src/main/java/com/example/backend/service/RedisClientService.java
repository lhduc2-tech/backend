package com.example.backend.service;

import com.example.backend.config.RedisProperties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(RedisProperties.class)
public class RedisClientService {

    private final RedisProperties properties;
    private final HttpClient httpClient;

    public RedisClientService(RedisProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public boolean ping() {
        if (properties.restUrl() == null || properties.restToken() == null) {
            return false;
        }

        try {
            String baseUrl = properties.restUrl().replaceAll("/+$", "");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/ping"))
                    .header("Authorization", "Bearer " + properties.restToken())
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body().contains("PONG");
        } catch (Exception e) {
            return false;
        }
    }
}
