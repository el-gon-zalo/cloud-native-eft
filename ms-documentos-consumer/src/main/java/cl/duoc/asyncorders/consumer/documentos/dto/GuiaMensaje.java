package cl.duoc.asyncorders.consumer.documentos.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el mensaje de Guia publicado en la cola "cola.guias"
 * por el microservicio productor (ms-guias / API Gateway).
 *
 * Los nombres de campos coinciden 1:1 con el JSON devuelto por el
 * endpoint POST /api/guias para que Jackson2JsonMessageConverter
 * pueda deserializar el mensaje automáticamente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiaMensaje implements Serializable {

	private Long id;
	private String ventaId;
	private String cliente;
	private String transportistaId;
	private String direccionEntrega;
	private String fechaDespacho;
	private String estado;
	private String s3Url;
	private String fechaCreacion;
	private String fechaActualizacion;
	private String mensaje;
}
