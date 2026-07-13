package cl.duoc.asyncorders.consumer.notificaciones.service;

import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.consumer.notificaciones.dto.VentaMensaje;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificacionService {

	/**
	 * Procesa el envío de notificación
	 */
	public void enviarNotificacion(VentaMensaje mensaje) {
		log.info("Iniciando envío de notificación para VentaID: {}", mensaje.getVentaId());

		try {
			// Simular envío de email
			Thread.sleep(1000); // Simula procesamiento de 1 segundo

			String contenidoEmail = generarContenidoEmail(mensaje);

			log.info("Notificación enviada exitosamente");
			log.info("Email enviado a: {}", mensaje.getEmail());
			log.info("Contenido del email:\n{}", contenidoEmail);

		} catch (InterruptedException e) {
			log.error("Error al enviar notificación: {}", e.getMessage());
			Thread.currentThread().interrupt();
			throw new RuntimeException("Error al enviar notificación", e);
		}
	}

	/**
	 * Genera el contenido del email de notificación
	 */
	private String generarContenidoEmail(VentaMensaje mensaje) {

		StringBuilder sb = new StringBuilder();

		sb.append("═══════════════════════════════════════════════════════\n");
		sb.append("           CONFIRMACIÓN DE COMPRA - AsyncOrders        \n");
		sb.append("═══════════════════════════════════════════════════════\n\n");

		sb.append("Estimado/a ").append(mensaje.getCliente()).append(",\n\n");

		sb.append("¡Gracias por su compra!\n\n");

		sb.append("Hemos recibido su pedido correctamente.\n\n");

		sb.append("DETALLES DE LA COMPRA:\n");
		sb.append("─────────────────────────────────────────────────────\n");
		sb.append("Número de orden: ").append(mensaje.getVentaId()).append("\n");
		sb.append("Fecha: ").append(mensaje.getFechaVenta()).append("\n");
		sb.append("Total: $").append(String.format("%,.0f", mensaje.getTotal())).append("\n");
		sb.append("Cantidad de productos: ").append(mensaje.getProductos().size()).append("\n\n");

		sb.append("Su documento comercial está siendo generado y será\n");
		sb.append("enviado a su correo electrónico en breve.\n\n");

		sb.append("Si tiene alguna consulta, no dude en contactarnos.\n\n");

		sb.append("Saludos cordiales,\n");
		sb.append("Equipo AsyncOrders\n");
		sb.append("═══════════════════════════════════════════════════════\n");

		return sb.toString();
	}
}
