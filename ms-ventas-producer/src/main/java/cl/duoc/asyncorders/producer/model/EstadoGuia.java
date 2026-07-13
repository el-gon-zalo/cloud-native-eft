package cl.duoc.asyncorders.producer.model;

public enum EstadoGuia {
	PENDIENTE, // Guía creada, documento aún no generado/subido a S3
	GENERADA, // Documento generado y subido a S3
	DESPACHADA, // Entregada al transportista
	ENTREGADA, // Entregada al cliente final
	ANULADA // Eliminada/anulada
}
