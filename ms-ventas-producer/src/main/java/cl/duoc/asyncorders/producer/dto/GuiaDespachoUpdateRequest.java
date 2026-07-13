package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;
import java.time.LocalDate;

import cl.duoc.asyncorders.producer.model.EstadoGuia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualización parcial de una guía. Todos los campos son opcionales:
 * solo se actualizan los que vengan distintos de null.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespachoUpdateRequest implements Serializable {

	private String transportistaId;

	private String direccionEntrega;

	private LocalDate fechaDespacho;

	private EstadoGuia estado;
}
