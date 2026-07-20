package cl.duoc.asyncorders.consumer.documentos.dto;

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
public class CursoDTO implements Serializable {

	private Long id;
	private String nombre;
	private Integer cupo;
	private String profesorACargoUsername;
	private List<String> listaInscritosUsernames;
	private String estado;
}