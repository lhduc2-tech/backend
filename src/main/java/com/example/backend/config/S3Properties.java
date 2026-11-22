package com.example.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for S3-compatible object storage.
 */
@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String endpoint,
        String region,
        String keyId,
        String accessKey,
        String bucketName
) {
}
