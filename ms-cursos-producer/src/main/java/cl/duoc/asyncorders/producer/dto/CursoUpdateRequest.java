package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;

import cl.duoc.asyncorders.producer.model.EstadoCurso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoUpdateRequest implements Serializable {
	private String nombre;
	private Integer cupo;
	private String profesorACargoUsername;
	private EstadoCurso estado;
}