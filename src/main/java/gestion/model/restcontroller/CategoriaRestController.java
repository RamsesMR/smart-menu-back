package gestion.model.restcontroller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.Categoria;
import gestion.model.collections.DTO.CategoriaDto;
import gestion.model.service.CategoriaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaRestController {

    private final CategoriaService categoriaService;

    private CategoriaDto toDto(Categoria categoria) {
        return CategoriaDto.builder()
                .id(categoria.getId() != null ? categoria.getId().toHexString() : null)
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .orden(categoria.getOrden())
                .activo(categoria.isActivo())
                .build();
    }

    @GetMapping
    public ResponseEntity<?> dameTodas() {
        List<CategoriaDto> categorias = categoriaService.findAll()
                .stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> dameUna(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            return ResponseEntity.badRequest().body("ID inválido");
        }

        Categoria categoria = categoriaService.findById(new ObjectId(id));
        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(categoria));
    }

    @PostMapping
    public ResponseEntity<?> inserta(@RequestBody Categoria categoria) {
        Categoria guardada = categoriaService.insertOne(categoria);
        if (guardada == null) {
            return ResponseEntity.status(409).body("La categoría ya existe");
        }

        return ResponseEntity.status(201).body(toDto(guardada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edita(@PathVariable("id") String id, @RequestBody Categoria categoria) {
        if (!ObjectId.isValid(id)) {
            return ResponseEntity.badRequest().body("ID inválido");
        }

        categoria.setId(new ObjectId(id));
        Categoria actualizada = categoriaService.updateOne(categoria);
        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borra(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            return ResponseEntity.badRequest().body("ID inválido");
        }

        int resultado = categoriaService.deleteOne(new ObjectId(id));
        if (resultado == 1) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}