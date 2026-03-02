package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.service.RestauranteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/restaurante")
@RequiredArgsConstructor
public class RestauranteRestController {

    private final RestauranteService restauranteService;

    @GetMapping
    public ResponseEntity<?> todos() {
        return ResponseEntity.ok(restauranteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> uno(@PathVariable ObjectId id) {
        var restaurante = restauranteService.findById(id);
        if (restaurante == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(restaurante);
    }
}