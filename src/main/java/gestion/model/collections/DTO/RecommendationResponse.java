package gestion.model.collections.DTO;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationResponse {
  private int kcalObjetivo;
  private List<MenuSuggestion> menus;
}