package cl.duoc.asyncorders.consumer.documentos.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cl.duoc.asyncorders.consumer.documentos.dto.GuiaMensaje;
import cl.duoc.asyncorders.consumer.documentos.service.GuiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuiaListener {

	private final GuiaService guiaService;

	/**
	 * Listener que consume mensajes de la cola "cola.guias"
	 */
	@RabbitListener(queues = "${rabbitmq.queue.guias}")
	public void procesarMensajeGuia(GuiaMensaje mensaje, Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		log.info("Mensaje recibido en cola.guias");
		log.info("GuiaID: {}, VentaID: {}, Cliente: {}", mensaje.getId(), mensaje.getVentaId(),
				mensaje.getCliente());

		try {
			guiaService.procesarGuia(mensaje);

			// Confirmar mensaje (ACK)
			channel.basicAck(deliveryTag, false);
			log.info("Mensaje de guía procesado y confirmado (ACK)");

		} catch (Exception e) {
			log.error("Error al procesar mensaje de guía: {}", e.getMessage());

			try {
				// Rechazar mensaje. Si existe cola.guias.dlq configurada con
				// dead-letter-exchange, RabbitMQ la enrutará automáticamente allí.
				channel.basicNack(deliveryTag, false, false);
				log.warn("Mensaje de guía rechazado y enviado a DLQ (si está configurada)");
			} catch (IOException ioException) {
				log.error("Error al rechazar mensaje de guía: {}", ioException.getMessage());
			}
		}
	}
}
