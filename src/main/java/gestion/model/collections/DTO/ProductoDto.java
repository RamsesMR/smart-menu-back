package gestion.model.collections.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoDto {
    private String id;
    private String categoriaId;
    private String categoria;
    private String restauranteId;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal tipoIva;
    private BigDecimal importeIva;
    private BigDecimal precioConIva;
    private String imagen;
    private boolean disponible;
}