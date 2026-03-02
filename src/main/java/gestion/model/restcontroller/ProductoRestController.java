package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.Producto;
import gestion.model.service.ProductoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoRestController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<?> dameTodos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // GET /producto/disponibles?restauranteId=abc123
    @GetMapping("/disponibles")
    public ResponseEntity<?> disponibles(@RequestParam("restauranteId") String restauranteId) {
        if (!ObjectId.isValid(restauranteId)) return ResponseEntity.badRequest().body("ID inválido");
        return ResponseEntity.ok(productoService.findDisponiblesByRestaurante(new ObjectId(restauranteId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> dameUno(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        Producto producto = productoService.findById(new ObjectId(id));
        if (producto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<?> inserta(@RequestBody Producto producto) {
        Producto guardado = productoService.insertOne(producto);
        if (guardado == null) return ResponseEntity.status(409).body("El producto ya existe");
        return ResponseEntity.status(201).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifica(@PathVariable("id") String id, @RequestBody Producto producto) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        producto.setId(new ObjectId(id));
        Producto actualizado = productoService.updateOne(producto);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> elimina(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        int resultado = productoService.deleteOne(new ObjectId(id));
        if (resultado == 1) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}