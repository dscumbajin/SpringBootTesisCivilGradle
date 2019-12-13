package uce.edu.ec.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uce.edu.ec.app.model.Bien;

@Repository
public interface BienesRepository extends JpaRepository<Bien, Integer> {

	@Query(value = "SELECT * FROM Bienes WHERE fecha_ingreso BETWEEN :startDate AND :endDate ORDER BY fecha_ingreso DESC", nativeQuery = true)
	Page<Bien> findByPeriodo(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable page);

	// @Query( value = "SELECT * FROM BIENES WHERE ALTA = ?1", nativeQuery = true)
	@Query(value = "SELECT * FROM Bienes b WHERE b.alta like %:input% or b.anterior like %:input%"
			+ " or b.serie like %:input% or b.descripcion like %:input%", nativeQuery = true)
	Page<Bien> findByInput(@Param("input") String input, Pageable page);

	public Bien findByAlta(String alta);

	// valor repetido por alta, anterior, serie
	boolean existsByAltaAndAnteriorAndSerie(String alta, String anterior, String serie);

	// Lista de bienes que se pueden asignar
	List<Bien> findByControl(String Control);

}
