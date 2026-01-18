package gestion.model.collections;

import java.io.Serializable;
import java.math.BigDecimal;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class LineaPedido implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private ObjectId productoId;
	private String nombreActual;
	private BigDecimal precioActual;
	private int cantidad;
	private String nota;
	

}
