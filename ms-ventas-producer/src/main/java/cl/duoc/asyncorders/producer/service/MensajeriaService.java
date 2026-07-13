package cl.duoc.asyncorders.producer.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.producer.dto.VentaMensaje;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MensajeriaService {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.exchange.ventas}")
	private String exchangeVentas;

	@Value("${rabbitmq.exchange.notificaciones}")
	private String exchangeNotificaciones;

	@Value("${rabbitmq.routing.documento.generar}")
	private String routingDocumentoGenerar;

	@Value("${rabbitmq.routing.notificacion.enviar}")
	private String routingNotificacionEnviar;

	/**
	 * Publica mensaje para generar documento
	 */
	public void publicarMensajeDocumento(VentaMensaje mensaje) {

		try {
			mensaje.setTipoMensaje("DOCUMENTO");
			rabbitTemplate.convertAndSend(exchangeVentas, routingDocumentoGenerar, mensaje);
			log.info("Mensaje de DOCUMENTO publicado: VentaID={}", mensaje.getVentaId());
		} catch (Exception e) {
			log.error("Error al publicar mensaje de documento: {}", e.getMessage());
			throw new RuntimeException("Error al publicar mensaje de documento", e);
		}
	}

	/**
	 * Publica mensaje para enviar notificación
	 */
	public void publicarMensajeNotificacion(VentaMensaje mensaje) {

		try {
			mensaje.setTipoMensaje("NOTIFICACION");
			rabbitTemplate.convertAndSend(exchangeNotificaciones, routingNotificacionEnviar, mensaje);
			log.info("Mensaje de NOTIFICACION publicado: VentaID={}", mensaje.getVentaId());
		} catch (Exception e) {
			log.error("Error al publicar mensaje de notificación: {}", e.getMessage());
			throw new RuntimeException("Error al publicar mensaje de notificación", e);
		}
	}

	/**
	 * Publica ambos mensajes (documento y notificación)
	 */
	public void publicarMensajesVenta(VentaMensaje mensaje) {

		publicarMensajeDocumento(mensaje);
		publicarMensajeNotificacion(mensaje);
		log.info("Mensajes de venta publicados exitosamente: VentaID={}", mensaje.getVentaId());
	}
}
