package gestion.model.collections.DTO;

import java.math.BigDecimal;
import java.util.List;

import gestion.model.collections.Producto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuSuggestion {
  private String tipo; // "COMPLETO", "LIGERO", "ENERGIA"
  private int kcalTotal;
  private BigDecimal proteTotal;
  private BigDecimal grasasTotal;
  private BigDecimal carbTotal;

  private List<Producto> productos;
  private String reason;
}
