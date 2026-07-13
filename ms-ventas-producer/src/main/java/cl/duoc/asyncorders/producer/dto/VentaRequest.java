package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class VentaRequest implements Serializable {

	@NotBlank(message = "El ID de venta es obligatorio")
	private String ventaId;

	@NotBlank(message = "El cliente es obligatorio")
	private String cliente;

	@NotBlank(message = "El RUT es obligatorio")
	private String rut;

	@NotEmpty(message = "Debe incluir al menos un producto")
	@Valid
	private List<ProductoDTO> productos;

	@NotNull(message = "El total es obligatorio")
	@Positive(message = "El total debe ser mayor a 0")
	private Double total;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email debe ser válido")
	private String email;
}
