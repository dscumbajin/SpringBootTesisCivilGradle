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

	@Query( value = "SELECT * FROM BIENES WHERE ALTA = ?1", nativeQuery = true)
//	@Query(value = "SELECT * FROM Bienes b WHERE b.alta like %:input% or b.anterior like %:input%"
//			+ " or b.serie like %:input% or b.descripcion like %:input% ORDER BY garantia DESC", nativeQuery = true)
	Page<Bien> findByInput(@Param("alta") String input, Pageable page);
	
	//Lista todos los bienes oredenados por fecha de ingreso descendente
	@Query( value = "SELECT * FROM BIENES b, DETALLES d WHERE b.id_detalle = d.id ORDER BY b.fecha_ingreso DESC", nativeQuery = true)
	Page<Bien> findAllOrdenado(Pageable page);
	
	public Bien findByAlta(String alta);

	// valor repetido por alta nueva
	boolean existsByAlta(String alta);

	// valor repetido por alta anterior
	boolean existsByAnterior(String anterior);
	
	// valor repetido por serie
	boolean existsBySerie(String serie);
	
	// valor repetido por alta, anterior, serie
	boolean existsByAltaAndAnterior(String alta, String anterior);
	

	// Lista de bienes que se pueden asignar
	@Query(value = "select * from BIENES where control = ? order by id desc", nativeQuery = true )
	List<Bien> findByControl(@Param("control") String Control);

}
