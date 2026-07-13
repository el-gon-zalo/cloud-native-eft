package cl.duoc.asyncorders.consumer.notificaciones.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cl.duoc.asyncorders.consumer.notificaciones.dto.VentaMensaje;
import cl.duoc.asyncorders.consumer.notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionListener {

	private final NotificacionService notificacionService;

	/**
	 * Listener que consume mensajes de la cola de notificaciones
	 */
	@RabbitListener(queues = "${rabbitmq.queue.notificaciones}")
	public void procesarMensajeNotificacion(VentaMensaje mensaje, Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		log.info("Mensaje recibido en cola.notificaciones");
		log.info("VentaID: {}, Cliente: {}, Email: {}, Tipo: {}", mensaje.getVentaId(), mensaje.getCliente(),
				mensaje.getEmail(), mensaje.getTipoMensaje());

		try {
			// Procesar la notificación
			notificacionService.enviarNotificacion(mensaje);

			// Confirmar mensaje (ACK)
			channel.basicAck(deliveryTag, false);
			log.info("Mensaje procesado y confirmado (ACK)");

		} catch (Exception e) {
			log.error("Error al procesar mensaje: {}", e.getMessage());

			try {
				// Rechazar mensaje y enviarlo a DLQ (NACK)
				channel.basicNack(deliveryTag, false, false);
				log.warn("Mensaje rechazado y enviado a DLQ");
			} catch (IOException ioException) {
				log.error("Error al rechazar mensaje: {}", ioException.getMessage());
			}
		}
	}

	/**
	 * Listener que monitorea la Dead Letter Queue de notificaciones
	 */
	@RabbitListener(queues = "${rabbitmq.queue.notificaciones.dlq}")
	public void procesarMensajesDlq(VentaMensaje mensaje, Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		log.error("Mensaje en DLQ - cola.notificaciones.dlq");
		log.error("VentaID: {}, Cliente: {}, Email: {}", mensaje.getVentaId(), mensaje.getCliente(),
				mensaje.getEmail());
		log.error("Este mensaje requiere revisión manual");

		try {

			channel.basicAck(deliveryTag, false);
			log.info("Mensaje DLQ confirmado");
		} catch (IOException e) {
			log.error("Error al confirmar mensaje DLQ: {}", e.getMessage());
		}
	}
}
