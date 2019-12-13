package uce.edu.ec.app.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uce.edu.ec.app.model.Bien;
import uce.edu.ec.app.repository.BienesRepository;

@Service
public class BienesServiceJPA implements IBienService {

	@Autowired
	BienesRepository bienesRepo;

	@Override
	public void insertar(Bien bien) {
		bienesRepo.save(bien);

	}

	@Override
	public List<Bien> buscarTodas() {
		return bienesRepo.findAll();
	}

	@Override
	public Bien buscarPorId(int idBien) {
		Optional<Bien> optional = bienesRepo.findById(idBien);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void eliminar(int idBien) {
		bienesRepo.deleteById(idBien);

	}

	@Override
	public Page<Bien> buscarTodas(Pageable page) {
		return bienesRepo.findAll(page);
	}

	@Override
	public Bien buscarPorAlta(String alta) {
		return bienesRepo.findByAlta(alta);
	}

	@Override
	public boolean existePorId(int id) {

		return bienesRepo.existsById(id);
	}

	@Override
	public boolean exiteRegistroPorAltaAnteriorSerie(String alta, String anterior, String serie) {
		return bienesRepo.existsByAltaAndAnteriorAndSerie(alta, anterior, serie);
	}

	@Override
	public List<Bien> sinAsignacion() {
		return bienesRepo.findByControl("Activo");
	}

	@Override
	public Page<Bien> search(String input, Pageable page) {
		return bienesRepo.findByInput(input, page);
	}

	@Override
	public Page<Bien> buscarPeriodo(Date startDate, Date endDate, Pageable page) {
		return bienesRepo.findByPeriodo(startDate, endDate, page);
	}

}
