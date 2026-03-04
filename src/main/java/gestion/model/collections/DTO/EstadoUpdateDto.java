package gestion.model.collections.DTO;

import gestion.model.enums.EstadoPedido;
import lombok.Data;

@Data
public class EstadoUpdateDto {
    private EstadoPedido estado;
}