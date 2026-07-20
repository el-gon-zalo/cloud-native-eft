package cl.duoc.asyncorders.producer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import cl.duoc.asyncorders.producer.model.Curso;
import cl.duoc.asyncorders.producer.model.EstadoCurso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoResponse implements Serializable {

    private Long id;

    private String nombre;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    private Integer cupo;

    private String profesorACargoUsername;

    private EstadoCurso estado;

    private List<String> listaInscritosUsernames;

    private String s3Key;

    private String s3Url;

    public static CursoResponse fromEntity(Curso curso) {

        return CursoResponse.builder()
                .id(curso.getId())
                .nombre(curso.getNombre())
                .fechaCreacion(curso.getFechaCreacion())
                .fechaActualizacion(curso.getFechaActualizacion())
                .cupo(curso.getCupo())
                .profesorACargoUsername(curso.getProfesorACargoUsername())
                .estado(curso.getEstado())
                .listaInscritosUsernames(curso.getListaInscritosUsernames())
                .s3Key(curso.getS3Key())
                .s3Url(curso.getS3Url())
                .build();
    }
}