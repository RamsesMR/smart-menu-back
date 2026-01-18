package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;

import gestion.model.collections.Usuario;
import gestion.model.collections.DTO.UsuarioDto;


public interface UsuarioService {
	Usuario findById(ObjectId usuarioID);
	List<Usuario> findAll();
	
	List<UsuarioDto> findAllDto();
	UsuarioDto findByIdDto(ObjectId usuarioID);
	
	
	Usuario updateOne(Usuario usuario);
	Usuario insertOne(Usuario usuario);
	int deleteOne(ObjectId UsuarioId);
	Usuario buscarPorUsernamePassword(String username, String password);
	Usuario buscaPorEmail(String email);

}
