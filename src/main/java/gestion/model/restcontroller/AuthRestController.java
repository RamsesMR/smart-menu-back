package gestion.model.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.Usuario;
import gestion.model.collections.DTO.AuthResponseDto;
import gestion.model.collections.DTO.RegisterRequestDto;
import gestion.model.collections.DTO.UsuarioDto;
import gestion.model.collections.DTO.UsuarioLoginDto;
import gestion.model.enums.Rol;
import gestion.model.repository.UsuarioRepository;
import gestion.security.JwtSecurityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

  private final UsuarioRepository usuarioRepo;
  private final JwtSecurityService jwtService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody UsuarioLoginDto body) {

    Usuario user = usuarioRepo.findByEmail(body.getEmail());
    if (user == null) return ResponseEntity.status(401).body("Credenciales inválidas");

    if (!passwordEncoder.matches(body.getPassword(), user.getContrasena())) {
      return ResponseEntity.status(401).body("Credenciales inválidas");
    }

    String token = jwtService.generateToken(user.getEmail(), user.getAuthorities());

    UsuarioDto dto = UsuarioDto.builder()
        .nombre(user.getNombre())
        .rol(user.getRol().name())
        .build();

    return ResponseEntity.ok(AuthResponseDto.builder()
        .token(token)
        .user(dto)
        .build());
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDto body) {

    if (usuarioRepo.findByEmail(body.getEmail()) != null) {
      return ResponseEntity.status(409).body("Email ya registrado");
    }

    Rol rol = Rol.valueOf(body.getRol()); // "EMPRESA"/"CLIENTE" (valida si quieres)

    Usuario nuevo = Usuario.builder()
        .nombre(body.getNombre())
        .email(body.getEmail())
        .contrasena(passwordEncoder.encode(body.getPassword())) // ✅ BCrypt
        .rol(rol)
        .build();

    Usuario guardado = usuarioRepo.save(nuevo);

    String token = jwtService.generateToken(guardado.getEmail(), guardado.getAuthorities());

    UsuarioDto dto = UsuarioDto.builder()
        .nombre(guardado.getNombre())
        .rol(guardado.getRol().name())
        .build();

    return ResponseEntity.status(201).body(AuthResponseDto.builder()
        .token(token)
        .user(dto)
        .build());
  }
}
