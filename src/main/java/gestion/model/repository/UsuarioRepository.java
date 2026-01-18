package gestion.model.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gestion.model.collections.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, ObjectId> {
	
	Usuario findByEmail(String email);
	
	Usuario findByEmailAndContrasena(String email, String contrasena);

}
