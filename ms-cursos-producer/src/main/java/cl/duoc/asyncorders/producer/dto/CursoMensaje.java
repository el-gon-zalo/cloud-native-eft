package cl.duoc.asyncorders.producer.dto;

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
public class CursoMensaje implements Serializable {

	private Long cursoId;
	private String nombreCurso;
	private String profesorACargoUsername;
	private Integer cupo;
	private List<String> inscritosUsernames;
	private String estado;
	private String fechaCreacion;
	private String tipoMensaje; // "DOCUMENTO" o "NOTIFICACION"
}