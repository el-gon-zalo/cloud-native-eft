package cl.duoc.asyncorders.producer.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.asyncorders.producer.dto.GuiaDespachoRequest;
import cl.duoc.asyncorders.producer.dto.GuiaDespachoResponse;
import cl.duoc.asyncorders.producer.dto.GuiaDespachoUpdateRequest;
import cl.duoc.asyncorders.producer.model.GuiaDespacho;
import cl.duoc.asyncorders.producer.service.GuiaDespachoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints para el ciclo de vida de una guía de despacho.
 * En este dominio "venta" y "guía de despacho" son equivalentes: la guía es
 * el documento que respalda el despacho de la venta.
 *
 * Todos los endpoints requieren autenticación (ver SecurityConfig).
 */
@Slf4j
@RestController
@RequestMapping("/api/guias")
@RequiredArgsConstructor
public class GuiaDespachoController {

	private final GuiaDespachoService guiaDespachoService;

	/**
	 * Crear guía de despacho.
	 * POST /api/guias
	 */
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	//@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	public ResponseEntity<GuiaDespachoResponse> crearGuia(@Valid @RequestBody GuiaDespachoRequest request) {

		log.info("Solicitud de creación de guía recibida: ventaId={}", request.getVentaId());

		GuiaDespacho guia = guiaDespachoService.crearGuia(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(GuiaDespachoResponse.fromEntity(guia, "Guía creada exitosamente"));
	}

	/**
	 * Subir la guía generada a S3.
	 * POST /api/guias/{id}/documento
	 */
	@PostMapping("/{id}/documento")
	@PreAuthorize("isAuthenticated()")
	//@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	public ResponseEntity<GuiaDespachoResponse> subirDocumento(@PathVariable Long id) {

		log.info("Solicitud de subida a S3 para guía id={}", id);

		GuiaDespacho guia = guiaDespachoService.subirDocumentoAS3(id);

		return ResponseEntity.ok(GuiaDespachoResponse.fromEntity(guia, "Documento subido a S3 exitosamente"));
	}

	/**
	 * Descargar guía, validando permisos (dueño de la venta, transportista asignado o admin).
	 * GET /api/guias/{id}/descarga
	 * Retorna una URL prefirmada de S3 con vigencia limitada (10 minutos).
	 */
	@GetMapping("/{id}/descarga")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<java.util.Map<String, String>> descargarGuia(@PathVariable Long id,
			Authentication authentication) {

		log.info("Solicitud de descarga de guía id={} por usuario={}", id, authentication.getName());

		String urlDescarga = guiaDespachoService.descargarGuia(id, authentication);

		return ResponseEntity.ok(java.util.Map.of(
				"urlDescarga", urlDescarga,
				"vigenciaMinutos", "10"));
	}

	/**
	 * Modificar o actualizar una guía.
	 * PUT /api/guias/{id}
	 */
	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	//@PreAuthorize("hasAnyRole('ADMIN', 'TRANSPORTISTA')")
	public ResponseEntity<GuiaDespachoResponse> actualizarGuia(@PathVariable Long id,
			@RequestBody GuiaDespachoUpdateRequest request) {

		log.info("Solicitud de actualización de guía id={}", id);

		GuiaDespacho guia = guiaDespachoService.actualizarGuia(id, request);

		return ResponseEntity.ok(GuiaDespachoResponse.fromEntity(guia, "Guía actualizada exitosamente"));
	}

	/**
	 * Eliminar una guía específica.
	 * DELETE /api/guias/{id}
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminarGuia(@PathVariable Long id) {

		log.info("Solicitud de eliminación de guía id={}", id);

		guiaDespachoService.eliminarGuia(id);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Consultar guías por transportista y fecha.
	 * GET /api/guias/transportista/{transportistaId}?fecha=2026-07-12
	 * GET /api/guias/transportista/{transportistaId}?fecha=2026-07-01&fechaHasta=2026-07-31
	 */
	@GetMapping("/transportista/{transportistaId}")
	@PreAuthorize("isAuthenticated()")
	//@PreAuthorize("hasAnyRole('ADMIN', 'TRANSPORTISTA')")
	public ResponseEntity<List<GuiaDespachoResponse>> consultarPorTransportistaYFecha(
			@PathVariable String transportistaId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
			Authentication authentication) {

		boolean esAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (!esAdmin && !authentication.getName().equals(transportistaId)) {
			throw new cl.duoc.asyncorders.producer.exception.AccesoDenegadoException(
					"Un transportista solo puede consultar sus propias guías");
		}

		log.info("Consulta de guías: transportistaId={}, fecha={}, fechaHasta={}", transportistaId, fecha,
				fechaHasta);

		List<GuiaDespachoResponse> respuesta = guiaDespachoService
				.consultarPorTransportistaYFecha(transportistaId, fecha, fechaHasta).stream()
				.map(g -> GuiaDespachoResponse.fromEntity(g, null))
				.toList();

		return ResponseEntity.ok(respuesta);
	}

	/**
	 * Obtener todas las guías de despacho.
	 * GET /api/guias
	 */
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	//@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'TRANSPORTISTA')")
	public ResponseEntity<List<GuiaDespachoResponse>> obtenerTodasLasGuias() {

		log.info("Solicitud para obtener todas las guías");

		List<GuiaDespachoResponse> respuesta = guiaDespachoService
				.obtenerTodasLasGuias()
				.stream()
				.map(g -> GuiaDespachoResponse.fromEntity(g, null))
				.toList();

		return ResponseEntity.ok(respuesta);
	}
}
