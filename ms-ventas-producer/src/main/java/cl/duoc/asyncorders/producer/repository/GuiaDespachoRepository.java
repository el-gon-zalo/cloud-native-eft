package cl.duoc.asyncorders.producer.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.asyncorders.producer.model.GuiaDespacho;

@Repository
public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

	Optional<GuiaDespacho> findByVentaId(String ventaId);

	/** Consulta por transportista y una fecha exacta de despacho */
	List<GuiaDespacho> findByTransportistaIdAndFechaDespacho(String transportistaId, LocalDate fechaDespacho);

	/** Consulta por transportista dentro de un rango de fechas (para búsquedas por período) */
	List<GuiaDespacho> findByTransportistaIdAndFechaDespachoBetween(String transportistaId, LocalDate desde,
			LocalDate hasta);
}
