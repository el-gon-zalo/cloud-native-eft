package cl.duoc.asyncorders.consumer.documentos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.consumer.documentos.dto.CursoMensaje;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CursoService {

	/**
	 * Procesa el curso recibido desde RabbitMQ.
	 *
	 * Aquí es donde iría la lógica real: generar el PDF/documento del curso,
	 * subirlo a S3, y (si corresponde) actualizar el registro en la base de
	 * datos del productor con la URL resultante (vía REST, otra cola, etc.).
	 * Por ahora se deja simulado para validar el flujo end-to-end.
	 */
	public void procesarCurso(CursoMensaje curso) {
		log.info("Iniciando procesamiento de curso. CursoID: {}, Nombre: {}", curso.getCursoId(), curso.getNombreCurso());

		String contenido = generarContenidoCurso(curso);

		log.info("Curso procesado exitosamente");
		log.info("Contenido generado:\n{}", contenido);

		// TODO: reemplazar por la subida real a S3 (AWS SDK) cuando esté disponible
		String urlSimulada = almacenarEnS3(curso.getCursoId(), contenido);

		log.info("Documento de curso disponible en: {}", urlSimulada);
	}

	private String generarContenidoCurso(CursoMensaje curso) {
		StringBuilder sb = new StringBuilder();

		sb.append("═══════════════════════════════════════════════════════\n");
		sb.append("                     CURSO                               \n");
		sb.append("═══════════════════════════════════════════════════════\n\n");

		sb.append("ID Curso: ").append(curso.getCursoId()).append("\n");
		sb.append("Nombre: ").append(curso.getNombreCurso()).append("\n");
		sb.append("Estado: ").append(curso.getEstado()).append("\n\n");

		sb.append("DATOS DEL CURSO:\n");
		sb.append("Cupo: ").append(curso.getCupo()).append("\n");
		sb.append("Profesor a cargo: ").append(curso.getProfesorACargoUsername()).append("\n\n");

		sb.append("INSCRITOS:\n");
		sb.append(formatearInscritos(curso.getInscritosUsernames())).append("\n");

		sb.append("═══════════════════════════════════════════════════════\n");

		return sb.toString();
	}

	private String formatearInscritos(List<String> inscritos) {
		if (inscritos == null || inscritos.isEmpty()) {
			return "(sin inscritos)\n";
		}

		StringBuilder sb = new StringBuilder();
		for (String username : inscritos) {
			sb.append("- ").append(username).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Simula el almacenamiento del documento generado en S3.
	 * Reemplazar por una integración real con AWS SDK (S3Client.putObject)
	 * cuando el bucket y las credenciales estén disponibles.
	 */
	private String almacenarEnS3(Long cursoId, String contenido) {
		log.info("Almacenando documento de curso: CURSO-{}.txt (simulado)", cursoId);
		return "https://s3.amazonaws.com/bucket-simulado/cursos/CURSO-" + cursoId + ".txt";
	}
}