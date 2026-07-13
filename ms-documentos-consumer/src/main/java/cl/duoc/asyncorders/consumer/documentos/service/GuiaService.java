package cl.duoc.asyncorders.consumer.documentos.service;

import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.consumer.documentos.dto.GuiaMensaje;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GuiaService {

	/**
	 * Procesa la guía de despacho recibida desde RabbitMQ.
	 *
	 * Aquí es donde iría la lógica real: generar el PDF/documento de la guía,
	 * subirlo a S3, y (si corresponde) actualizar el registro en la base de
	 * datos del productor con la URL resultante (vía REST, otra cola, etc.).
	 * Por ahora se deja simulado para validar el flujo end-to-end.
	 */
	public void procesarGuia(GuiaMensaje guia) {
		log.info("Iniciando procesamiento de guía. VentaID: {}, GuiaID: {}", guia.getVentaId(), guia.getId());

		String contenido = generarContenidoGuia(guia);

		log.info("Guía procesada exitosamente");
		log.info("Contenido generado:\n{}", contenido);

		// TODO: reemplazar por la subida real a S3 (AWS SDK) cuando esté disponible
		String urlSimulada = almacenarEnS3(guia.getVentaId(), contenido);

		log.info("Documento de guía disponible en: {}", urlSimulada);
	}

	private String generarContenidoGuia(GuiaMensaje guia) {
		StringBuilder sb = new StringBuilder();

		sb.append("═══════════════════════════════════════════════════════\n");
		sb.append("              GUÍA DE DESPACHO                          \n");
		sb.append("═══════════════════════════════════════════════════════\n\n");

		sb.append("ID Guía: ").append(guia.getId()).append("\n");
		sb.append("ID Venta: ").append(guia.getVentaId()).append("\n");
		sb.append("Estado: ").append(guia.getEstado()).append("\n\n");

		sb.append("DATOS DEL CLIENTE:\n");
		sb.append("Cliente: ").append(guia.getCliente()).append("\n\n");

		sb.append("DATOS DE DESPACHO:\n");
		sb.append("Transportista: ").append(guia.getTransportistaId()).append("\n");
		sb.append("Dirección de entrega: ").append(guia.getDireccionEntrega()).append("\n");
		sb.append("Fecha de despacho: ").append(guia.getFechaDespacho()).append("\n\n");

		sb.append("═══════════════════════════════════════════════════════\n");

		return sb.toString();
	}

	/**
	 * Simula el almacenamiento del documento generado en S3.
	 * Reemplazar por una integración real con AWS SDK (S3Client.putObject)
	 * cuando el bucket y las credenciales estén disponibles.
	 */
	private String almacenarEnS3(String ventaId, String contenido) {
		log.info("Almacenando documento de guía: GUIA-{}.txt (simulado)", ventaId);
		return "https://s3.amazonaws.com/bucket-simulado/guias/GUIA-" + ventaId + ".txt";
	}
}
