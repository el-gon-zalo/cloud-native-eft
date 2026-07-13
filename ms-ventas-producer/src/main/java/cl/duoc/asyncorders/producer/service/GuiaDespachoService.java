package cl.duoc.asyncorders.producer.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.producer.dto.GuiaDespachoRequest;
import cl.duoc.asyncorders.producer.dto.GuiaDespachoUpdateRequest;
import cl.duoc.asyncorders.producer.exception.AccesoDenegadoException;
import cl.duoc.asyncorders.producer.exception.RecursoNoEncontradoException;
import cl.duoc.asyncorders.producer.model.EstadoGuia;
import cl.duoc.asyncorders.producer.model.GuiaDespacho;
import cl.duoc.asyncorders.producer.repository.GuiaDespachoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuiaDespachoService {

	private final GuiaDespachoRepository guiaDespachoRepository;
	private final S3Service s3Service;

	/**
	 * Crea una nueva guía de despacho (estado inicial: PENDIENTE, sin documento en S3 todavía).
	 */
	public GuiaDespacho crearGuia(GuiaDespachoRequest request) {

		guiaDespachoRepository.findByVentaId(request.getVentaId()).ifPresent(g -> {
			throw new IllegalArgumentException("Ya existe una guía asociada a la venta " + request.getVentaId());
		});

		GuiaDespacho guia = GuiaDespacho.builder()
				.ventaId(request.getVentaId())
				.cliente(request.getCliente())
				.clienteUsername(request.getClienteUsername())
				.rutCliente(request.getRutCliente())
				.emailCliente(request.getEmailCliente())
				.transportistaId(request.getTransportistaId())
				.direccionEntrega(request.getDireccionEntrega())
				.fechaDespacho(request.getFechaDespacho())
				.estado(EstadoGuia.PENDIENTE)
				.build();

		GuiaDespacho guardada = guiaDespachoRepository.save(guia);
		log.info("Guía de despacho creada: id={}, ventaId={}", guardada.getId(), guardada.getVentaId());

		return guardada;
	}

	/**
	 * Genera el contenido de la guía y lo sube a S3, actualizando la guía con la key/url resultante.
	 */
	public GuiaDespacho subirDocumentoAS3(Long id) {

		GuiaDespacho guia = obtenerPorId(id);

		String contenido = generarContenidoGuia(guia);
		String key = "guias/" + guia.getVentaId() + ".txt";

		s3Service.subirDocumento(key, contenido.getBytes(StandardCharsets.UTF_8), "text/plain");

		guia.setS3Key(key);
		guia.setS3Url(String.format("s3://%s", key));
		guia.setEstado(EstadoGuia.GENERADA);

		GuiaDespacho actualizada = guiaDespachoRepository.save(guia);
		log.info("Documento de guía {} subido a S3 con key {}", guia.getVentaId(), key);

		return actualizada;
	}

	/**
	 * Genera una URL de descarga prefirmada, validando primero que el usuario autenticado
	 * tenga permisos sobre la guía (dueño de la venta, transportista asignado o administrador).
	 */
	public String descargarGuia(Long id, Authentication authentication) {

		GuiaDespacho guia = obtenerPorId(id);

		validarPermisoDescarga(guia, authentication);

		if (guia.getS3Key() == null) {
			throw new IllegalStateException("La guía aún no tiene un documento generado en S3");
		}

		return s3Service.generarUrlPrefirmada(guia.getS3Key(), Duration.ofMinutes(10));
	}

	/**
	 * Actualiza campos de una guía existente (actualización parcial).
	 */
	public GuiaDespacho actualizarGuia(Long id, GuiaDespachoUpdateRequest request) {

		GuiaDespacho guia = obtenerPorId(id);

		if (request.getTransportistaId() != null) {
			guia.setTransportistaId(request.getTransportistaId());
		}
		if (request.getDireccionEntrega() != null) {
			guia.setDireccionEntrega(request.getDireccionEntrega());
		}
		if (request.getFechaDespacho() != null) {
			guia.setFechaDespacho(request.getFechaDespacho());
		}
		if (request.getEstado() != null) {
			guia.setEstado(request.getEstado());
		}

		GuiaDespacho actualizada = guiaDespachoRepository.save(guia);
		log.info("Guía {} actualizada", actualizada.getVentaId());

		return actualizada;
	}

	/**
	 * Elimina una guía específica.
	 */
	public void eliminarGuia(Long id) {

		GuiaDespacho guia = obtenerPorId(id);
		guiaDespachoRepository.delete(guia);
		log.info("Guía {} eliminada", guia.getVentaId());
	}

	/**
	 * Consulta guías por transportista y fecha (fecha exacta, o rango si se entrega fechaHasta).
	 */
	public List<GuiaDespacho> consultarPorTransportistaYFecha(String transportistaId, LocalDate fecha,
			LocalDate fechaHasta) {

		if (fechaHasta != null) {
			return guiaDespachoRepository.findByTransportistaIdAndFechaDespachoBetween(transportistaId, fecha,
					fechaHasta);
		}

		return guiaDespachoRepository.findByTransportistaIdAndFechaDespacho(transportistaId, fecha);
	}

	public GuiaDespacho obtenerPorId(Long id) {
		return guiaDespachoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe una guía con id " + id));
	}

	public List<GuiaDespacho> obtenerTodasLasGuias() {
    return guiaDespachoRepository.findAll();
	}

	private void validarPermisoDescarga(GuiaDespacho guia, Authentication authentication) {

		String username = authentication.getName();
		boolean esAdmin = tieneRol(authentication, "ROLE_ADMIN");
		boolean esTransportistaAsignado = tieneRol(authentication, "ROLE_TRANSPORTISTA")
				&& username.equals(guia.getTransportistaId());
		boolean esClienteDueno = tieneRol(authentication, "ROLE_CLIENTE")
				&& username.equalsIgnoreCase(guia.getClienteUsername());

		if (!esAdmin && !esTransportistaAsignado && !esClienteDueno) {
			throw new AccesoDenegadoException(
					"El usuario '" + username + "' no tiene permisos para descargar esta guía");
		}
	}

	private boolean tieneRol(Authentication authentication, String rol) {
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			if (authority.getAuthority().equals(rol)) {
				return true;
			}
		}
		return false;
	}

	private String generarContenidoGuia(GuiaDespacho guia) {
		StringBuilder sb = new StringBuilder();
		sb.append("GUIA DE DESPACHO\n");
		sb.append("Venta ID: ").append(guia.getVentaId()).append("\n");
		sb.append("Cliente: ").append(guia.getCliente()).append(" (").append(guia.getRutCliente()).append(")\n");
		sb.append("Direccion de entrega: ").append(guia.getDireccionEntrega()).append("\n");
		sb.append("Transportista: ").append(guia.getTransportistaId()).append("\n");
		sb.append("Fecha de despacho: ").append(guia.getFechaDespacho()).append("\n");
		return sb.toString();
	}
}
