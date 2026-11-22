package com.example.backend.controller;

import com.example.backend.config.S3Properties;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public HealthController(JdbcTemplate jdbcTemplate, S3Client s3Client, S3Properties s3Properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        boolean dbUp = isDatabaseUp();
        boolean s3Up = isS3Up();
        boolean allUp = dbUp && s3Up;

        HttpStatus status = allUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(Map.of(
                "status", allUp ? "ok" : "error",
                "database", dbUp ? "up" : "down",
                "s3", s3Up ? "up" : "down"
        ));
    }

    private boolean isDatabaseUp() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isS3Up() {
        try {
            var response = s3Client.headBucket(builder -> builder.bucket(s3Properties.bucketName()));
            return response.sdkHttpResponse().isSuccessful();
        } catch (Exception ex) {
            return false;
        }
    }
}
