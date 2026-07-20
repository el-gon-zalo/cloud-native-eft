package cl.duoc.asyncorders.consumer.documentos.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO implements Serializable {

	private String codigo;
	private String nombre;
	private Integer cantidad;
	private Double precio;
}
