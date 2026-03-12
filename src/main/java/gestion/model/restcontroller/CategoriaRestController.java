package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.Categoria;
import gestion.model.service.CategoriaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaRestController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<?> dameTodas() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> dameUna(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        Categoria categoria = categoriaService.findById(new ObjectId(id));
        if (categoria == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<?> inserta(@RequestBody Categoria categoria) {
        Categoria guardada = categoriaService.insertOne(categoria);
        if (guardada == null) return ResponseEntity.status(409).body("La categoría ya existe");
        return ResponseEntity.status(201).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edita(@PathVariable("id") String id, @RequestBody Categoria categoria) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        categoria.setId(new ObjectId(id));
        Categoria actualizada = categoriaService.updateOne(categoria);
        if (actualizada == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borra(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        int resultado = categoriaService.deleteOne(new ObjectId(id));
        if (resultado == 1) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}