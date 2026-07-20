package cl.duoc.asyncorders.producer.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	/**
	 * Sube el contenido de la guía a S3 y retorna la key generada.
	 */
	public String subirDocumento(String key, byte[] contenido, String contentType) {

		log.info("Subiendo documento a S3: bucket={}, key={}", bucketName, key);

		PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(contentType)
				.build();

		s3Client.putObject(request, RequestBody.fromBytes(contenido));

		log.info("Documento subido exitosamente a S3: {}", key);

		return key;
	}

	/**
	 * Descarga directamente los bytes del documento (requiere que quien llame ya haya validado permisos).
	 */
	public byte[] descargarDocumento(String key) {

		GetObjectRequest request = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

		try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)) {
			return response.readAllBytes();
		} catch (java.io.IOException e) {
			log.error("Error al descargar documento de S3: {}", e.getMessage());
			throw new RuntimeException("Error al descargar el documento desde S3", e);
		}
	}

	/**
	 * Genera una URL prefirmada de descarga, válida por un tiempo limitado.
	 * Es la forma recomendada de entregar acceso temporal sin exponer el bucket.
	 */
	public String generarUrlPrefirmada(String key, Duration vigencia) {

		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(vigencia)
				.getObjectRequest(getObjectRequest)
				.build();

		PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

		return presignedRequest.url().toString();
	}
}
