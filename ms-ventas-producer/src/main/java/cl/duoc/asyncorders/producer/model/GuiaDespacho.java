package cl.duoc.asyncorders.producer.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una guía de despacho.
 * En este dominio, "venta" y "guía de despacho" son el mismo concepto:
 * la guía es el documento que respalda el despacho de una venta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guias_despacho")
public class GuiaDespacho {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String ventaId;

	@Column(nullable = false)
	private String cliente;

	/** Username del cliente dueño de la guía, usado para validar permisos de descarga */
	@Column(nullable = false)
	private String clienteUsername;

	@Column(nullable = false)
	private String rutCliente;

	@Column(nullable = false)
	private String emailCliente;

	/** Identificador (username) del transportista asignado. Usado para validar permisos de descarga. */
	@Column(nullable = false)
	private String transportistaId;

	@Column(nullable = false)
	private String direccionEntrega;

	@Column(nullable = false)
	private LocalDate fechaDespacho;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoGuia estado;

	/** Key del objeto en el bucket S3 (ej: guias/GD-0001.txt) */
	private String s3Key;

	/** URL (o URL prefirmada) del documento en S3 */
	private String s3Url;

	private LocalDateTime fechaCreacion;

	private LocalDateTime fechaActualizacion;

	@PrePersist
	public void prePersist() {
		this.fechaCreacion = LocalDateTime.now();
		this.fechaActualizacion = LocalDateTime.now();
		if (this.estado == null) {
			this.estado = EstadoGuia.PENDIENTE;
		}
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaActualizacion = LocalDateTime.now();
	}
}
