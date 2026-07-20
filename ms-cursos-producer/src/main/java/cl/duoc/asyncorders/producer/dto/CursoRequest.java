package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;
import java.util.List;

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
public class CursoRequest implements Serializable {

	@NotBlank(message = "El nombre del curso es obligatorio")
	private String nombre;

	@NotNull(message = "El cupo es obligatorio")
	@Positive(message = "El cupo debe ser mayor a 0")
	private Integer cupo;

	@NotBlank(message = "El username del profesor a cargo es obligatorio")
	private String profesorACargoUsername;

	@NotEmpty(message = "Debe incluir al menos un usuario inscrito")
	private List<String> listaInscritosUsernames;
}