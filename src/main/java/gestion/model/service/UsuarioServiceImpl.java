package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gestion.model.collections.Usuario;
import gestion.model.collections.DTO.UsuarioDto;
import gestion.model.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Mapeo a DTO (sin datos sensibles)
    private UsuarioDto toDto(Usuario u) {
        if (u == null) return null;
        return UsuarioDto.builder()
                .nombre(u.getNombre())
                .rol(u.getRol().name())
                .build();
    }

    private List<UsuarioDto> toDtoList(List<Usuario> lista) {
        if (lista == null) return List.of();
        return lista.stream().map(this::toDto).toList();
    }

    // UsuarioService 
    @Override
    public Usuario findById(ObjectId usuarioId) {
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<UsuarioDto> findAllDto() {
        return toDtoList(usuarioRepository.findAll());
    }

    @Override
    public UsuarioDto findByIdDto(ObjectId usuarioId) {
        return toDto(usuarioRepository.findById(usuarioId).orElse(null));
    }

    @Override
    public Usuario insertOne(Usuario usuario) {
        if (usuario.getId() == null || !usuarioRepository.existsById(usuario.getId())) {
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    @Override
    public Usuario updateOne(Usuario usuario) {
        if (usuarioRepository.existsById(usuario.getId())) {
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    @Override
    public int deleteOne(ObjectId usuarioId) {
        if (usuarioRepository.existsById(usuarioId)) {
            usuarioRepository.deleteById(usuarioId);
            return 1;
        }
        return 0;
    }

    @Override
    public Usuario buscaPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // UserDetailsService (Spring Security)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }
        return user;
    }
}