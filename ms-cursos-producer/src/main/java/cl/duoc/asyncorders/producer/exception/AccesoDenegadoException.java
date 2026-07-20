package cl.duoc.asyncorders.producer.exception;

public class AccesoDenegadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccesoDenegadoException(String mensaje) {
		super(mensaje);
	}
}
