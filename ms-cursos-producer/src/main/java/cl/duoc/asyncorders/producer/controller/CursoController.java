package cl.duoc.asyncorders.producer.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.asyncorders.producer.dto.CursoRequest;
import cl.duoc.asyncorders.producer.dto.CursoResponse;
import cl.duoc.asyncorders.producer.dto.CursoUpdateRequest;
import cl.duoc.asyncorders.producer.model.Curso;
import cl.duoc.asyncorders.producer.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints para el ciclo de vida de un curso.
 *
 * Todos los endpoints requieren autenticación (ver SecurityConfig).
 * La autorización fina (dueño del curso, admin, etc.) se valida en CursoService.
 */
@Slf4j
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

	private final CursoService cursoService;

	/**
	 * Crear curso.
	 * POST /api/cursos
	 */
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<CursoResponse> crearCurso(@Valid @RequestBody CursoRequest request,
			Authentication authentication) {
		log.info("Solicitud de creación de curso recibida: nombre={}, solicitante={}",
				request.getNombre(), authentication.getName());
		Curso curso = cursoService.crearCurso(request, authentication);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(CursoResponse.fromEntity(curso));
				//.body(CursoResponse.fromEntity(curso, "Curso creado exitosamente"));
	}

	/**
	 * Obtener un curso por id.
	 * GET /api/cursos/{id}
	 */
	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<CursoResponse> obtenerCursoPorId(@PathVariable Long id) {
		log.info("Solicitud para obtener curso id={}", id);
		Curso curso = cursoService.obtenerCursoPorId(id);
		return ResponseEntity.ok(CursoResponse.fromEntity(curso));
	}

	/**
	 * Obtener todos los cursos.
	 * GET /api/cursos
	 */
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<CursoResponse>> obtenerTodosLosCursos() {
		log.info("Solicitud para obtener todos los cursos");
		List<CursoResponse> respuesta = cursoService
				.obtenerTodosLosCursos()
				.stream()
				.map(c -> CursoResponse.fromEntity(c))
				.toList();
		return ResponseEntity.ok(respuesta);
	}

	/**
	 * Modificar o actualizar un curso (actualización parcial).
	 * PUT /api/cursos/{id}
	 */
	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<CursoResponse> actualizarCurso(@PathVariable Long id,
			@RequestBody CursoUpdateRequest request, Authentication authentication) {
		log.info("Solicitud de actualización de curso id={}, solicitante={}", id, authentication.getName());
		Curso curso = cursoService.actualizarCurso(id, request, authentication);
		return ResponseEntity.ok(CursoResponse.fromEntity(curso));
	}

	/**
	 * Eliminar un curso específico.
	 * DELETE /api/cursos/{id}
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> eliminarCurso(@PathVariable Long id, Authentication authentication) {
		log.info("Solicitud de eliminación de curso id={}, solicitante={}", id, authentication.getName());
		cursoService.eliminarCurso(id, authentication);
		return ResponseEntity.noContent().build();
	}

    /**
	 * Health check
	 */
	@GetMapping("/health")
	public ResponseEntity<String> health() {

		return ResponseEntity.ok("Productor de cursos funcionando correctamente");
	}
}