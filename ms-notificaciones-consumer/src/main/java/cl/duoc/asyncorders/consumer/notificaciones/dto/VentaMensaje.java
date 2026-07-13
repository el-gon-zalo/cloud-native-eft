package cl.duoc.asyncorders.consumer.notificaciones.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaMensaje implements Serializable {

	private String ventaId;
	private String cliente;
	private String rut;
	private List<ProductoDTO> productos;
	private Double total;
	private String email;
	private String fechaVenta;
	private String tipoMensaje;
}
