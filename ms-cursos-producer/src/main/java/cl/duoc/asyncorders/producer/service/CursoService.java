package cl.duoc.asyncorders.producer.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import cl.duoc.asyncorders.producer.dto.CursoRequest;
import cl.duoc.asyncorders.producer.dto.CursoUpdateRequest;
import cl.duoc.asyncorders.producer.exception.AccesoDenegadoException;
import cl.duoc.asyncorders.producer.exception.RecursoNoEncontradoException;
import cl.duoc.asyncorders.producer.model.Curso;
import cl.duoc.asyncorders.producer.model.EstadoCurso;
import cl.duoc.asyncorders.producer.repository.CursoRepository;
import cl.duoc.asyncorders.producer.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cl.duoc.asyncorders.producer.dto.CursoMensaje;

@Slf4j
@Service
@RequiredArgsConstructor
public class CursoService {

	private final CursoRepository cursoRepository;
	private final MensajeriaService mensajeriaService;

	//POST
	public Curso crearCurso(CursoRequest request, Authentication authentication) {

		String username = AuthUtils.username(authentication);

		//Temporal
		log.info("Usuario: {}", AuthUtils.username(authentication));

		authentication.getAuthorities().forEach(a ->
		log.info("Authority: {}", a.getAuthority())
		);



		boolean esAdmin = AuthUtils.tieneRol(authentication, "ROLE_ADMIN");
		boolean esElProfesorAsignado = username.equals(request.getProfesorACargoUsername());

		
		if (!esAdmin && !esElProfesorAsignado) {
			throw new AccesoDenegadoException(
					"Solo un administrador o el propio profesor a cargo pueden crear este curso");
		}

		Curso curso = Curso.builder()
				.nombre(request.getNombre())
				.cupo(request.getCupo())
				.profesorACargoUsername(request.getProfesorACargoUsername())
				.listaInscritosUsernames(request.getListaInscritosUsernames())
				.estado(EstadoCurso.PENDIENTE)
				.build();

		Curso guardado = cursoRepository.save(curso);
		log.info("Curso creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());

		CursoMensaje mensaje = CursoMensaje.builder()
        .cursoId(guardado.getId())
        .nombreCurso(guardado.getNombre())
        .profesorACargoUsername(guardado.getProfesorACargoUsername())
        .cupo(guardado.getCupo())
        .inscritosUsernames(guardado.getListaInscritosUsernames())
        .estado(guardado.getEstado().name())
        .fechaCreacion(guardado.getFechaCreacion().toString())
        .build();

		mensajeriaService.publicarTodosLosMensajesCurso(mensaje);

		log.info("Mensajes RabbitMQ enviados para el curso {}", guardado.getId());

		return guardado;
	}

	//PUT
	public Curso actualizarCurso(Long id, CursoUpdateRequest request, Authentication authentication) {

		Curso curso = obtenerPorId(id);

		String username = AuthUtils.username(authentication);
		boolean esAdmin = AuthUtils.tieneRol(authentication, "ROLE_ADMIN");
		boolean esProfesorDelCurso = username.equals(curso.getProfesorACargoUsername());

		if (!esAdmin && !esProfesorDelCurso) {
			throw new AccesoDenegadoException(
					"Solo un administrador o el profesor a cargo pueden modificar este curso");
		}

		if (request.getNombre() != null) {
			curso.setNombre(request.getNombre());
		}
		if (request.getCupo() != null) {
			curso.setCupo(request.getCupo());
		}
		if (request.getProfesorACargoUsername() != null) {
			// Solo un ADMIN puede reasignar el curso a otro profesor
			if (!esAdmin) {
				throw new AccesoDenegadoException(
						"Solo un administrador puede reasignar el profesor a cargo del curso");
			}
			curso.setProfesorACargoUsername(request.getProfesorACargoUsername());
		}
		if (request.getEstado() != null) {
			curso.setEstado(request.getEstado());
		}

		Curso actualizado = cursoRepository.save(curso);
		log.info("Curso {} actualizado", actualizado.getId());

		return actualizado;
	}

	//DELETE
	public void eliminarCurso(Long id, Authentication authentication) {

		Curso curso = obtenerPorId(id);

		String username = AuthUtils.username(authentication);
		boolean esAdmin = AuthUtils.tieneRol(authentication, "ROLE_ADMIN");
		boolean esProfesorDelCurso = username.equals(curso.getProfesorACargoUsername());

		if (!esAdmin && !esProfesorDelCurso) {
			throw new AccesoDenegadoException(
					"Solo un administrador o el profesor a cargo pueden eliminar este curso");
		}

		cursoRepository.delete(curso);
		log.info("Curso {} eliminado", curso.getId());
	}

    //GET
	public Curso obtenerCursoPorId(Long id) {
		return obtenerPorId(id);
	}

	public List<Curso> obtenerTodosLosCursos() {
		return cursoRepository.findAll();
	}

	private Curso obtenerPorId(Long id) {
		return cursoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe un curso con id " + id));
	}

    //Generar contenido curso como String
    private String generarContenidoCurso(Curso curso) {
    StringBuilder sb = new StringBuilder();
    sb.append("CURSO\n");
    sb.append("Curso ID: ").append(curso.getId()).append("\n");
    sb.append("Nombre: ").append(curso.getNombre()).append("\n");
    sb.append("Profesor a cargo: ").append(curso.getProfesorACargoUsername()).append("\n");
    sb.append("Cupo: ").append(curso.getCupo()).append("\n");
    sb.append("Inscritos: ").append(curso.getListaInscritosUsernames()).append("\n");
    sb.append("Estado: ").append(curso.getEstado()).append("\n");
    return sb.toString();
}

}