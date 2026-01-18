package gestion.model.collections;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Document(collection  = "producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	private ObjectId categoriaId;
	private String nombre;
	private String descripcion;
	private BigDecimal precio;
	private BigDecimal tipoIva;
	private BigDecimal importeIva;
	private BigDecimal precioConIva;
	private String imagen;
	private boolean disponible;
	
	
	public void calcularIva() {
	    if (this.precio == null) this.precio = BigDecimal.ZERO;
	    if (this.tipoIva == null) this.tipoIva = BigDecimal.ZERO;

	    
	    this.importeIva = this.precio
	            .multiply(this.tipoIva)
	            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

	    
	    this.precioConIva = this.precio
	            .add(this.importeIva)
	            .setScale(2, RoundingMode.HALF_UP);
	}
}
