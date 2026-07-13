package cl.duoc.asyncorders.consumer.documentos.service;

import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.consumer.documentos.dto.ProductoDTO;
import cl.duoc.asyncorders.consumer.documentos.dto.VentaMensaje;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentoService {

	/**
	 * Procesa la generación del documento comercial
	 */
	public void generarDocumento(VentaMensaje mensaje) {
		log.info("Iniciando generación de documento para VentaID: {}", mensaje.getVentaId());

		try {
			// Simular generación de documento
			Thread.sleep(2000); // Simula procesamiento de 2 segundos

			String documento = generarContenidoDocumento(mensaje);

			log.info("Documento generado exitosamente");
			log.info("Contenido del documento:\n{}", documento);

			// Aquí iría la lógica para almacenar al documento
			almacenarDocumento(mensaje.getVentaId(), documento);

		} catch (InterruptedException e) {
			log.error("Error al generar documento: {}", e.getMessage());
			Thread.currentThread().interrupt();
			throw new RuntimeException("Error al generar documento", e);
		}
	}

	/**
	 * Genera el contenido del documento comercial
	 */
	private String generarContenidoDocumento(VentaMensaje mensaje) {

		StringBuilder sb = new StringBuilder();

		sb.append("═══════════════════════════════════════════════════════\n");
		sb.append("              DOCUMENTO COMERCIAL - VENTA              \n");
		sb.append("═══════════════════════════════════════════════════════\n\n");

		sb.append("ID de Venta: ").append(mensaje.getVentaId()).append("\n");
		sb.append("Fecha: ").append(mensaje.getFechaVenta()).append("\n\n");

		sb.append("DATOS DEL CLIENTE:\n");
		sb.append("Nombre: ").append(mensaje.getCliente()).append("\n");
		sb.append("RUT: ").append(mensaje.getRut()).append("\n");
		sb.append("Email: ").append(mensaje.getEmail()).append("\n\n");

		sb.append("DETALLE DE PRODUCTOS:\n");
		sb.append("─────────────────────────────────────────────────────\n");

		double subtotal = 0;
		for (ProductoDTO producto : mensaje.getProductos()) {
			double lineaTotal = producto.getCantidad() * producto.getPrecio();
			subtotal += lineaTotal;

			sb.append(String.format("%-15s %-30s\n", producto.getCodigo(), producto.getNombre()));
			sb.append(String.format("  Cantidad: %d x $%,.0f = $%,.0f\n", producto.getCantidad(), producto.getPrecio(),
					lineaTotal));
			sb.append("\n");
		}

		sb.append("─────────────────────────────────────────────────────\n");
		sb.append(String.format("TOTAL: $%,.0f\n", mensaje.getTotal()));
		sb.append("═══════════════════════════════════════════════════════\n");

		return sb.toString();
	}

	/**
	 * Almacena el documento (simulado)
	 */
	private void almacenarDocumento(String ventaId, String contenido) {

		log.info("Almacenando documento: DOC-{}.txt", ventaId);
		// Aquí iría la lógica para guardar en S3, file system, etc.
		log.info("Documento almacenado exitosamente");
	}
}
