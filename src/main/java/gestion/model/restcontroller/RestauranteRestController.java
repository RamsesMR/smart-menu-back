package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gestion.model.service.RestauranteService;

@RestController
@RequestMapping("/restaurante")
public class RestauranteRestController {

	@Autowired
	private RestauranteService restauranteService;
	
	@GetMapping
	public ResponseEntity<?> todos(){
		return ResponseEntity.ok().body(restauranteService.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> uno(@PathVariable ObjectId id){
		return ResponseEntity.ok().body(restauranteService.findById(id));
	}
}
 