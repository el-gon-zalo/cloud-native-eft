package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO implements Serializable {

	@NotBlank(message = "El código del producto es obligatorio")
	private String codigo;

	@NotBlank(message = "El nombre del producto es obligatorio")
	private String nombre;

	@NotNull(message = "La cantidad es obligatoria")
	@Positive(message = "La cantidad debe ser mayor a 0")
	private Integer cantidad;

	@NotNull(message = "El precio es obligatorio")
	@Positive(message = "El precio debe ser mayor a 0")
	private Double precio;
}
