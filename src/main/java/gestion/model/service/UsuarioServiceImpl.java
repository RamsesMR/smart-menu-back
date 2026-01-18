package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gestion.model.collections.Usuario;
import gestion.model.collections.DTO.UsuarioDto;
import gestion.model.repository.UsuarioRepository;
@Service
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {
	
	private UsuarioDto toDto(Usuario u) {
	    if (u == null) return null;
	    return UsuarioDto.builder()
	            .nombre(u.getNombre())
	            .rol(u.getRol().name())
	            .build();
	}

	private List<UsuarioDto> toDtoList(List<Usuario> lista) {
	    if (lista == null) return null;
	    return lista.stream()
	    		.map(this::toDto)
	    		.toList();
	}

	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public Usuario findById(ObjectId usuarioID) {
		// TODO Auto-generated method stub
		return usuarioRepository.findById(usuarioID).orElse(null);
	}

	@Override
	public List<Usuario> findAll() {
		// TODO Auto-generated method stub
		return usuarioRepository.findAll();
	}

	@Override
	public Usuario updateOne(Usuario usuario) {
		// TODO Auto-generated method stub
		if(usuarioRepository.existsById(usuario.getId())) {
			
			return usuarioRepository.save(usuario);
		}
		return null;
	}

	@Override
	public Usuario insertOne(Usuario usuario) {
		// TODO Auto-generated method stub
	if(usuario.getId() == null || !usuarioRepository.existsById(usuario.getId())) {
			
			return usuarioRepository.save(usuario);
		}
		return null;
	}
	
	@Override
	public Usuario buscarPorUsernamePassword(String username, String password) {
		// TODO Auto-generated method stub
		return usuarioRepository.findByEmailAndContrasena(username, password);
	}

	@Override
	public int deleteOne(ObjectId UsuarioId) {
		// TODO Auto-generated method stub
		if(usuarioRepository.existsById(UsuarioId)) {
			usuarioRepository.deleteById(UsuarioId);
			return 1;
		}
		return 0;
	}

	@Override
	public Usuario buscaPorEmail(String email) {
		// TODO Auto-generated method stub
		return usuarioRepository.findByEmail(email);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Usuario user = usuarioRepository.findByEmail(email);
		if(user == null) {
			
			throw new UsernameNotFoundException("Usuario no encontrado");
		}else {
			return user;
		}
		
	}

	@Override
	public List<UsuarioDto> findAllDto() {
		
		List<Usuario> usuarios = usuarioRepository.findAll();
		return toDtoList(usuarios);
	}

	@Override
	public UsuarioDto findByIdDto(ObjectId usuarioID) {
		Usuario usuario = usuarioRepository.findById(usuarioID).orElse(null);
		return toDto(usuario);
	}

}
