package gestion.model.collections.DTO;

import java.math.BigDecimal;
import java.util.List;

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

    private List<String> tags;
    private List<String> alergenos;

    private Integer kcal;
    private BigDecimal proteinas;
    private BigDecimal grasas;
    private BigDecimal carbohidratos;
}