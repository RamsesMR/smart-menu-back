package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import gestion.model.collections.Producto;
import gestion.model.service.ProductoService;

@Controller
@RequestMapping("/producto")
public class ProductoRestController {

	@Autowired
	private ProductoService productoService;
	
	@GetMapping("/")
	public ResponseEntity<?> dameTodos(){
		
		return ResponseEntity.ok().body(productoService.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> dameUno(@PathVariable ObjectId id){
		
		return ResponseEntity.ok().body(productoService.findById(id));
	}
	
	@PostMapping("/")
	public ResponseEntity<?> inserta(@RequestBody Producto producto){
		
		return ResponseEntity.status(201).body(productoService.insertOne(producto));
	}
	
	@PutMapping("/")
	public ResponseEntity<?> modifica(@RequestBody Producto producto){
		return ResponseEntity.status(200).body(productoService.updateOne(producto));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> elimina(@PathVariable ObjectId id){
		
		int resultado = productoService.deleteOne(id);
		
		if(resultado == 1) {
			return ResponseEntity.noContent().build();
			
		}
		return ResponseEntity.notFound().build();
	}
}
