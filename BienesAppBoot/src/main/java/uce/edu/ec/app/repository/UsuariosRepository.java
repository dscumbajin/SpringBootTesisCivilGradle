package uce.edu.ec.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uce.edu.ec.app.model.Usuario;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Integer> {
	
	public Usuario findByCuenta (String cuenta);
	
	public boolean existsByCuentaAndEmail (String cuenta, String email);
	
	Usuario findByCuentaAndEmail(String cuenta, String email);

}
