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
public class MenuSuggestionDto {
    private String tipo;
    private int kcalTotal;
    private BigDecimal proteTotal;
    private BigDecimal grasasTotal;
    private BigDecimal carbTotal;

    private List<ProductoRecommendationDto> productos;
    private String reason;
}