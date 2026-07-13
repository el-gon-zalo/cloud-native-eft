package cl.duoc.asyncorders.producer.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.producer.dto.VentaMensaje;
import cl.duoc.asyncorders.producer.dto.VentaRequest;
import cl.duoc.asyncorders.producer.dto.VentaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaService {

	private final MensajeriaService mensajeriaService;

	/**
	 * Procesa una venta y publica mensajes asíncronos
	 */
	public VentaResponse procesarVenta(VentaRequest request) {

		log.info("Procesando venta: VentaID={}, Cliente={}", request.getVentaId(), request.getCliente());

		try {
			// 1. Validar venta (aquí podrías guardar en BD)
			log.info("Venta registrada en el sistema: {}", request.getVentaId());

			// 2. Crear mensaje para RabbitMQ
			VentaMensaje mensaje = VentaMensaje.builder().ventaId(request.getVentaId()).cliente(request.getCliente())
					.rut(request.getRut()).productos(request.getProductos()).total(request.getTotal())
					.email(request.getEmail())
					.fechaVenta(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build();

			// 3. Publicar mensajes asíncronos
			mensajeriaService.publicarMensajesVenta(mensaje);

			// 4. Responder inmediatamente al usuario
			return VentaResponse.builder().mensaje("Venta registrada exitosamente").ventaId(request.getVentaId())
					.timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).estado("PROCESANDO")
					.build();

		} catch (Exception e) {
			log.error("Error al procesar venta: {}", e.getMessage());
			throw new RuntimeException("Error al procesar la venta", e);
		}
	}
}
