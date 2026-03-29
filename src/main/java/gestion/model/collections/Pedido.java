package gestion.model.collections;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import gestion.model.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id; // ← CAMBIO: String en lugar de ObjectId

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId usuarioId;

    private String codigo;
    private String mesaId;
    private EstadoPedido estado;
    private String nota;
    private List<LineaPedido> lineasPedido;
    private BigDecimal totalPedido;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime fechaCreacion;
}