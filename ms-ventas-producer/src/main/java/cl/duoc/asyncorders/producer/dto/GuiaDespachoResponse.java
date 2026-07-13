package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import cl.duoc.asyncorders.producer.model.EstadoGuia;
import cl.duoc.asyncorders.producer.model.GuiaDespacho;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespachoResponse implements Serializable {

	private Long id;
	private String ventaId;
	private String cliente;
	private String transportistaId;
	private String direccionEntrega;
	private LocalDate fechaDespacho;
	private EstadoGuia estado;
	private String s3Url;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaActualizacion;
	private String mensaje;

	public static GuiaDespachoResponse fromEntity(GuiaDespacho guia, String mensaje) {
		return GuiaDespachoResponse.builder()
				.id(guia.getId())
				.ventaId(guia.getVentaId())
				.cliente(guia.getCliente())
				.transportistaId(guia.getTransportistaId())
				.direccionEntrega(guia.getDireccionEntrega())
				.fechaDespacho(guia.getFechaDespacho())
				.estado(guia.getEstado())
				.s3Url(guia.getS3Url())
				.fechaCreacion(guia.getFechaCreacion())
				.fechaActualizacion(guia.getFechaActualizacion())
				.mensaje(mensaje)
				.build();
	}
}
