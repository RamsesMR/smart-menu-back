package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;

import gestion.model.collections.Usuario;
import gestion.model.collections.DTO.UsuarioDto;

public interface UsuarioService {
    Usuario findById(ObjectId usuarioId);
    List<Usuario> findAll();
    List<UsuarioDto> findAllDto();
    UsuarioDto findByIdDto(ObjectId usuarioId);
    Usuario insertOne(Usuario usuario);
    Usuario updateOne(Usuario usuario);
    int deleteOne(ObjectId usuarioId);
    Usuario buscaPorEmail(String email);
}