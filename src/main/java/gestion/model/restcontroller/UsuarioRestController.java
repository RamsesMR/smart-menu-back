package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    // Devuelve DTOs: nunca expone contraseña ni datos sensibles
    @GetMapping
    public ResponseEntity<?> todos() {
        return ResponseEntity.ok(usuarioService.findAllDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> uno(@PathVariable ObjectId id) {
        var dto = usuarioService.findByIdDto(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borra(@PathVariable ObjectId id) {
        int resultado = usuarioService.deleteOne(id);
        if (resultado == 1) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    // Nota: creación de usuarios → /auth/register (con BCrypt)
    //       actualización de perfil biométrico → PUT /usuario/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualiza(@PathVariable ObjectId id,
                                       @RequestBody gestion.model.collections.Usuario usuario) {
        usuario.setId(id);
        var actualizado = usuarioService.updateOne(usuario);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }
}