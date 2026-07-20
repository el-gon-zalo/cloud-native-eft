package cl.duoc.asyncorders.producer.model;

public enum EstadoCurso {
	PENDIENTE, // Curso creado,  aún no generado/subido a S3
	GENERADO, // Curso generado y subido a S3
	ANULADO // Eliminado
}
