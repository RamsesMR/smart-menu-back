package gestion.model.restcontroller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gestion.model.collections.Usuario;
import gestion.model.service.UsuarioService;

@RestController

@RequestMapping("usuario")
public class UsuarioRestController {

	@Autowired
	private UsuarioService usuarioService;
	
	//ESTA RUTA IMPLEMENTA DTO DE USUARIO PARA NO DEVOLVER LOS DATOS SENSIBLES 
	@GetMapping("/")
	public ResponseEntity<?> todos(){
		return ResponseEntity.ok().body(usuarioService.findAllDto());
	}
	
	//ESTA OTRA RUTA TAMBIEN IMPLEMENTA EL DTO PARA NO DEOLVER SENSIBLES DEL USUARIO
	@GetMapping("/{id}")
	public ResponseEntity<?> uno(@PathVariable ObjectId id){
		return ResponseEntity.ok().body(usuarioService.findByIdDto(id));
	}
	
	@PostMapping("/")
	public ResponseEntity<?> insertta(@RequestBody Usuario usuario){
		return ResponseEntity.status(201).body(usuarioService.insertOne(usuario));
	}
	
	@PutMapping("/")
	public ResponseEntity<?> actua(@RequestBody Usuario usuario){
		return ResponseEntity.status(201).body(usuarioService.updateOne(usuario));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> borra (@PathVariable ObjectId id){
		
		int resultado = usuarioService.deleteOne(id);
		if(resultado == 1) {
			return ResponseEntity.noContent().build();
			
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
}
