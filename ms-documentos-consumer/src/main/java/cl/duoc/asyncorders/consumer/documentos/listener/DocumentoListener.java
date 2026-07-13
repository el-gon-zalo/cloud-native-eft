package cl.duoc.asyncorders.consumer.documentos.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cl.duoc.asyncorders.consumer.documentos.dto.VentaMensaje;
import cl.duoc.asyncorders.consumer.documentos.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentoListener {

	private final DocumentoService documentoService;

	/**
	 * Listener que consume mensajes de la cola de documentos
	 */
	@RabbitListener(queues = "${rabbitmq.queue.documentos}")
	public void procesarMensajeDocumento(VentaMensaje mensaje, Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		log.info("Mensaje recibido en cola.documentos");
		log.info("VentaID: {}, Cliente: {}, Tipo: {}", mensaje.getVentaId(), mensaje.getCliente(),
				mensaje.getTipoMensaje());

		try {
			// Procesar el documento
			documentoService.generarDocumento(mensaje);

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
	 * Listener que monitorea la Dead Letter Queue de documentos
	 */
	@RabbitListener(queues = "${rabbitmq.queue.documentos.dlq}")
	public void procesarMensajesDlq(VentaMensaje mensaje, Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		log.error("Mensaje en DLQ - cola.documentos.dlq");
		log.error("VentaID: {}, Cliente: {}", mensaje.getVentaId(), mensaje.getCliente());
		log.error("Este mensaje requiere revisión manual");

		try {
			// Aquí podría ir lógica de recuperación, notificación, etc
			channel.basicAck(deliveryTag, false);
			log.info("Mensaje DLQ confirmado");
		} catch (IOException e) {
			log.error("Error al confirmar mensaje DLQ: {}", e.getMessage());
		}
	}
}
