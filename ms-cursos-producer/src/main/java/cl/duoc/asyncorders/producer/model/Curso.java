package cl.duoc.asyncorders.producer.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cursos")
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private LocalDateTime fechaCreacion;

	private LocalDateTime fechaActualizacion;

	@Column(nullable = false)
	private Integer cupo;

	/** Username del profesor a cargo (usuario gestionado en Azure AD B2C) */
	@Column(nullable = false)
	private String profesorACargoUsername;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoCurso estado;

	/** Usernames de los usuarios (Azure AD B2C) inscritos en el curso */
	@ElementCollection
	@CollectionTable(name = "curso_inscritos", joinColumns = @JoinColumn(name = "curso_id"))
	@Column(name = "username")
	private List<String> listaInscritosUsernames;

	/** Key del objeto en el bucket S3 (ej: cursos/CUR-0001.txt) */
	private String s3Key;

	/** URL (o URL prefirmada) del documento en S3 */
	private String s3Url;

	@PrePersist
	public void prePersist() {
		this.fechaCreacion = LocalDateTime.now();
		this.fechaActualizacion = LocalDateTime.now();
		if (this.estado == null) {
			this.estado = EstadoCurso.PENDIENTE;
		}
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaActualizacion = LocalDateTime.now();
	}
}