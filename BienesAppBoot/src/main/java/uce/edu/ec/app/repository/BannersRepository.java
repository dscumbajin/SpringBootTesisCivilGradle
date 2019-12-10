package uce.edu.ec.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uce.edu.ec.app.model.Banner;

@Repository
public interface BannersRepository extends JpaRepository<Banner, Integer> {

	public List<Banner> findByEstatus(String estatus);

	public boolean existsByTitulo(String titulo);

}
