package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse implements Serializable {

	private String mensaje;
	private String ventaId;
	private String timestamp;
	private String estado;
}
