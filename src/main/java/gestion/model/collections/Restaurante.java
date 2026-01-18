package gestion.model.collections;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "restaurante")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class Restaurante implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
@Id
private ObjectId id;
private	String nombre;
private	String telefono;
private String direccion;
private boolean activo;

	

}
