package uce.edu.ec.app.service;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uce.edu.ec.app.model.Bien;

public interface IBienService {

	// Insertar
	void insertar(Bien bien);

	// Listar todos
	List<Bien> buscarTodas();

	// Buscar por id
	Bien buscarPorId(int idBien);

	// buecar por alta
	Bien buscarPorAlta(String alta);

	// existe por id
	boolean existePorId(int id);

	// Eliminar
	void eliminar(int idBien);

	// Para paginar
	Page<Bien> buscarTodas(Pageable page);

	// Buscar Bien por input
	Page<Bien> search(String input, Pageable page);

	// Buscar Bien por periodo de Registros paginado
	Page<Bien> buscarPeriodoPaginado(Date startDate, Date endDate, Pageable page);

	// Buscar Bien por periodo de Registros
	List<Bien> buscarPeriodo(Date startDate, Date endDate);

	// Para controlar repetidos
	boolean exiteRegistroPorAltaAnterior(String alta, String anterior);

	// Existe el registro por alta nueva
	boolean existeRegistroPorALta(String alta);

	// Existe el registro por alta anterior
	boolean existeRegistroPorAnterior(String anterior);

	// Existe el registro por serie
	boolean existeRegistroPorSerie(String serie);

	// Para buscar todos los bienes que no estan asignados
	List<Bien> sinAsignacion();

}
