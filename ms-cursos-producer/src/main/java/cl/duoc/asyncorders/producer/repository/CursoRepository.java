package cl.duoc.asyncorders.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.asyncorders.producer.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

}
