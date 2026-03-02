package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.DTO.EstadoUpdateDto;
import gestion.model.collections.Pedido;
import gestion.model.service.PedidoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
public class PedidoRestController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<?> dame() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> dameUno(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        Pedido pedido = pedidoService.findById(new ObjectId(id));
        if (pedido == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<?> porMesa(@PathVariable("mesaId") String mesaId) {
        return ResponseEntity.ok(pedidoService.findByMesaId(mesaId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> porUsuario(@PathVariable("usuarioId") String usuarioId) {
        if (!ObjectId.isValid(usuarioId)) return ResponseEntity.badRequest().body("ID inválido");
        return ResponseEntity.ok(pedidoService.findByUsuarioId(new ObjectId(usuarioId)));
    }

    @PostMapping
    public ResponseEntity<?> inserta(@RequestBody Pedido pedido) {
        pedido.setId(null); // dejamos que MongoDB genere el ID
        pedido.setCodigo(generarCodigo(pedido.getMesaId()));
        Pedido guardado = pedidoService.insertOne(pedido);
        if (guardado == null) return ResponseEntity.status(409).body("El pedido ya existe");
        return ResponseEntity.status(201).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edita(@PathVariable("id") String id, @RequestBody Pedido pedido) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        pedido.setId(new ObjectId(id));
        Pedido actualizado = pedidoService.updateOne(pedido);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable("id") String id,
                                           @RequestBody EstadoUpdateDto dto) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        Pedido actualizado = pedidoService.cambiarEstado(new ObjectId(id), dto.getEstado());
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borra(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) return ResponseEntity.badRequest().body("ID inválido");
        int resultado = pedidoService.deleteOne(new ObjectId(id));
        if (resultado == 1) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
    
    
    private String generarCodigo(String mesaId) {
        // Ej: "Mesa 1" -> "M1"
        String mesa = (mesaId == null) ? "M?" : mesaId.replaceAll("[^0-9]", "");
        if (mesa.isBlank()) mesa = "X";
        String prefix = "M" + mesa;

        // 4 chars random (rápido y suficiente)
        String rnd = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();

        return prefix + "-" + rnd;
    }
}