package cl.duoc.asyncorders.consumer.documentos.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cl.duoc.asyncorders.consumer.documentos.dto.CursoMensaje;
import cl.duoc.asyncorders.consumer.documentos.service.CursoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CursoListener {

	private final CursoService cursoService;

	/**
	 * Listener que consume mensajes de la cola "cola.cursos"
	 */
	@RabbitListener(queues = "${rabbitmq.queue.cursos}")
	public void procesarMensajeCurso(CursoMensaje mensaje, Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		log.info("Mensaje recibido en cola.cursos");
		log.info("CursoID: {}, Nombre: {}, Profesor: {}",
        mensaje.getCursoId(),
        mensaje.getNombreCurso(),
        mensaje.getProfesorACargoUsername());

		try {
			cursoService.procesarCurso(mensaje);

			// Confirmar mensaje (ACK)
			channel.basicAck(deliveryTag, false);
			log.info("Mensaje de curso procesado y confirmado (ACK)");

		} catch (Exception e) {
			log.error("Error al procesar mensaje de curso: {}", e.getMessage());

			try {
				// Rechazar mensaje. Si existe cola.cursos.dlq configurada con
				// dead-letter-exchange, RabbitMQ la enrutará automáticamente allí.
				channel.basicNack(deliveryTag, false, false);
				log.warn("Mensaje de curso rechazado y enviado a DLQ (si está configurada)");
			} catch (IOException ioException) {
				log.error("Error al rechazar mensaje de curso: {}", ioException.getMessage());
			}
		}
	}
}