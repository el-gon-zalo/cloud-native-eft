package cl.duoc.asyncorders.producer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.asyncorders.producer.dto.VentaRequest;
import cl.duoc.asyncorders.producer.dto.VentaResponse;
import cl.duoc.asyncorders.producer.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

	private final VentaService ventaService;

	/**
	 * Endpoint para registrar una nueva venta POST http://localhost:8081/api/ventas
	 */
	@PostMapping
	public ResponseEntity<VentaResponse> registrarVenta(@Valid @RequestBody VentaRequest request) {

		log.info("Solicitud de venta recibida: VentaID={}", request.getVentaId());

		VentaResponse response = ventaService.procesarVenta(request);

		log.info("Respuesta enviada al cliente: {}", response.getMensaje());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Health check
	 */
	@GetMapping("/health")
	public ResponseEntity<String> health() {

		return ResponseEntity.ok("Productor de ventas funcionando correctamente");
	}
}
