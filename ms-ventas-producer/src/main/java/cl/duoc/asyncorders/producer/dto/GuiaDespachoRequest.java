package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespachoRequest implements Serializable {

	@NotBlank(message = "El ID de venta es obligatorio")
	private String ventaId;

	@NotBlank(message = "El cliente es obligatorio")
	private String cliente;

	@NotBlank(message = "El username del cliente es obligatorio")
	private String clienteUsername;

	@NotBlank(message = "El RUT del cliente es obligatorio")
	private String rutCliente;

	@NotBlank(message = "El email del cliente es obligatorio")
	@Email(message = "El email debe ser válido")
	private String emailCliente;

	@NotBlank(message = "El transportista asignado es obligatorio")
	private String transportistaId;

	@NotBlank(message = "La dirección de entrega es obligatoria")
	private String direccionEntrega;

	@NotNull(message = "La fecha de despacho es obligatoria")
	@FutureOrPresent(message = "La fecha de despacho no puede ser en el pasado")
	private LocalDate fechaDespacho;
}
