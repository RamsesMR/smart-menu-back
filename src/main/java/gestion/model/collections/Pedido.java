package gestion.model.collections;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gestion.model.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Document(collection = "pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Pedido implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	private EstadoPedido estadoPedido;
	private String nota;
	private List<LineaPedido> items;
	private BigDecimal total;
	private LocalDate fechaCreacion;
	private String mesa;
	
}
