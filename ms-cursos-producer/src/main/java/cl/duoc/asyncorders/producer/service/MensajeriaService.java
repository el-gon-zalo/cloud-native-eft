package cl.duoc.asyncorders.producer.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.producer.dto.CursoMensaje;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MensajeriaService {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.exchange.cursos}")
	private String exchangeCursos;

	@Value("${rabbitmq.exchange.notificaciones}")
	private String exchangeNotificaciones;

	@Value("${rabbitmq.routing.curso.documento.generar}")
	private String routingCursoDocumentoGenerar;	

	@Value("${rabbitmq.routing.notificacion.enviar}")
	private String routingNotificacionEnviar;

	/**
	 * Publica mensaje para generar curso
	 */
	public void publicarMensajeCurso(CursoMensaje mensaje) {
		try {
			mensaje.setTipoMensaje("CURSO");
			rabbitTemplate.convertAndSend(
										exchangeCursos,
										routingCursoDocumentoGenerar,
										mensaje);
			log.info("Mensaje de CURSO publicado: CursoID={}", mensaje.getCursoId());
		} catch (Exception e) {
			log.error("Error al publicar mensaje de curso: {}", e.getMessage());
			throw new RuntimeException("Error al publicar mensaje de curso", e);
		}
	}

	/**
	 * Publica mensaje para enviar notificación
	 */
	public void publicarMensajeNotificacion(CursoMensaje mensaje) {
		try {
			mensaje.setTipoMensaje("NOTIFICACION");
			rabbitTemplate.convertAndSend(exchangeNotificaciones, routingNotificacionEnviar, mensaje);
			log.info("Mensaje de NOTIFICACION publicado: CursoID={}", mensaje.getCursoId());
		} catch (Exception e) {
			log.error("Error al publicar mensaje de notificación: {}", e.getMessage());
			throw new RuntimeException("Error al publicar mensaje de notificación", e);
		}
	}

	/**
	 * Publica ambos mensajes (curso y notificación)
	 */
	public void publicarTodosLosMensajesCurso(CursoMensaje mensaje) {
		publicarMensajeCurso(mensaje);
		publicarMensajeNotificacion(mensaje);
		log.info("Mensajes de curso publicados exitosamente: CursoID={}", mensaje.getCursoId());
	}
}