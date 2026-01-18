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

import gestion.model.collections.Pedido;
import gestion.model.service.PedidoService;

@Controller
@RequestMapping("/pedido")
public class PedidoRestController {

	@Autowired
	private PedidoService pedidoService;
	
	@GetMapping("/")
	public ResponseEntity<?> dame(){
		return ResponseEntity.ok().body(pedidoService.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> dameUno(@PathVariable ObjectId id){
		
		return ResponseEntity.ok().body(pedidoService.findById(id));
	}
	
	@PostMapping("/")
	public ResponseEntity<?> inserta(@RequestBody Pedido pedido){
		return ResponseEntity.status(201).body(pedidoService.insertOne(pedido));
	}
	
	@PutMapping("/")
	public ResponseEntity<?> edita(@RequestBody Pedido pedido){
		
		return ResponseEntity.status(201).body(pedidoService.updateOne(pedido));
	}
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> borra(@PathVariable ObjectId id){
		
		int resultado = pedidoService.deleteOne(id);
		
		if(resultado == 1) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}
