package gestion.model.restcontroller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.Categoria;
import gestion.model.collections.Producto;
import gestion.model.collections.DTO.ProductoDto;
import gestion.model.service.CategoriaService;
import gestion.model.service.ProductoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoRestController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    private ProductoDto toDto(Producto producto) {
        String categoriaNombre = null;

        if (producto.getCategoriaId() != null) {
            Categoria categoria = categoriaService.findById(producto.getCategoriaId());
            if (categoria != null) {
                categoriaNombre = categoria.getNombre();
            }
        }

        return ProductoDto.builder()
                .id(producto.getId() != null ? producto.getId().toHexString() : null)
                .categoriaId(producto.getCategoriaId() != null ? producto.getCategoriaId().toHexString() : null)
                .categoria(categoriaNombre)
                .restauranteId(producto.getRestauranteId() != null ? producto.getRestauranteId().toHexString() : null)
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .tipoIva(producto.getTipoIva())
                .importeIva(producto.getImporteIva())
                .precioConIva(producto.getPrecioConIva())
                .imagen(producto.getImagen())
                .disponible(producto.isDisponible())
                .build();
    }

    @GetMapping
    public ResponseEntity<?> dameTodos() {
        List<ProductoDto> productos = productoService.findAll()
                .stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> dameUno(@PathVariable String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");

        Producto producto = productoService.findById(new ObjectId(id));
        if (producto == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(toDto(producto));
    }

    @PostMapping
    public ResponseEntity<?> insertar(@RequestBody Producto producto) {
        Producto guardado = productoService.insertOne(producto);
        if (guardado == null) return ResponseEntity.status(409).body("El producto ya existe");

        return ResponseEntity.status(201).body(toDto(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable String id, @RequestBody Producto producto) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");

        producto.setId(new ObjectId(id));
        Producto actualizado = productoService.updateOne(producto);
        if (actualizado == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(toDto(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");

        int resultado = productoService.deleteOne(new ObjectId(id));
        if (resultado == 1) return ResponseEntity.noContent().build();

        return ResponseEntity.notFound().build();
    }
}