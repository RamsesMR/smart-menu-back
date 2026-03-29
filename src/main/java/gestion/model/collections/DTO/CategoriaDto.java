package gestion.model.collections.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaDto {
    private String id;
    private String nombre;
    private String descripcion;
    private int orden;
    private boolean activo;
}