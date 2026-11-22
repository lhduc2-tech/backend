package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.config.S3Properties;
import com.example.backend.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private S3Client s3Client;

	@Autowired
	private S3Presigner s3Presigner;

	@Autowired
	private S3Properties s3Properties;

	@Autowired
	private HealthController healthController;

	@Test
	void contextLoads() {
		assertThat(s3Client).isNotNull();
		assertThat(s3Presigner).isNotNull();
		assertThat(s3Properties.bucketName()).isNotBlank();
	}

	@Test
	void s3ConnectionWorks() {
		var response = s3Client.headBucket(builder -> builder.bucket(s3Properties.bucketName()));
		assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
	}

	@Test
	void healthEndpointReportsDatabaseAndS3Up() {
		var response = healthController.health();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).containsEntry("status", "ok")
				.containsEntry("database", "up")
				.containsEntry("s3", "up");
	}

}
