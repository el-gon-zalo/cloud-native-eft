package cl.duoc.asyncorders.producer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Value("${aws.region:us-east-1}")
	private String region;

	@Value("${aws.accessKeyId:}")
	private String accessKeyId;

	@Value("${aws.secretKey:}")
	private String secretKey;

	@Value("${aws.s3.endpoint:}")
	private String endpointOverride;

	@Bean
	public S3Client s3Client() {
		S3ClientBuilder builder = S3Client.builder()
				.region(Region.of(region))
				.credentialsProvider(credentialsProvider());

		if (endpointOverride != null && !endpointOverride.isBlank()) {
			// Útil para pruebas locales con LocalStack u otro emulador de S3
			builder.endpointOverride(java.net.URI.create(endpointOverride));
		}

		return builder.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		S3Presigner.Builder builder = S3Presigner.builder()
				.region(Region.of(region))
				.credentialsProvider(credentialsProvider());

		if (endpointOverride != null && !endpointOverride.isBlank()) {
			builder.endpointOverride(java.net.URI.create(endpointOverride));
		}

		return builder.build();
	}

	private software.amazon.awssdk.auth.credentials.AwsCredentialsProvider credentialsProvider() {
		if (accessKeyId != null && !accessKeyId.isBlank() && secretKey != null && !secretKey.isBlank()) {
			return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretKey));
		}
		// En AWS real (EC2/ECS/EKS) es preferible usar roles IAM en vez de claves estáticas
		return DefaultCredentialsProvider.create();
	}
}
