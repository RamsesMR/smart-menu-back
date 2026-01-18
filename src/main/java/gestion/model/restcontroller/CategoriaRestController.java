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

import gestion.model.collections.Categoria;
import gestion.model.service.CategoriaService;
import gestion.model.service.CategoriaServiceImpl;

@Controller
@RequestMapping("/categoria")
public class CategoriaRestController {

	@Autowired
	private CategoriaServiceImpl categoriaService;
	
	@GetMapping("/")
	public ResponseEntity<?> dameDame(){
		return ResponseEntity.ok().body(categoriaService.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> dameUno(@PathVariable ObjectId id){
		return ResponseEntity.ok().body(categoriaService.findById(id));
	}
	
	@PostMapping("/")
	public ResponseEntity<?> inserta(@RequestBody Categoria categoria){
		
		return ResponseEntity.status(201).body(categoriaService.insertOne(categoria));
	}
	
	@PutMapping("/")
	public ResponseEntity<?> edita(@RequestBody Categoria categoria){
		
		return ResponseEntity.status(201).body(categoriaService.updateOne(categoria));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> borraloCausita(@PathVariable ObjectId id){
		
		int resultado = categoriaService.deleteOne(id);
		if(resultado == 1) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
	
}
