package gestion.model.collections.DTO;

import java.util.List;
import org.bson.types.ObjectId;
import gestion.model.enums.DietType;
import gestion.model.enums.GoalType;
import lombok.Data;

@Data
public class RecommendationRequest {
  private ObjectId restauranteId;
  private Integer edad;
  private Double pesoKg;
  private Integer alturaCm;
  private DietType dieta;
  private GoalType objetivo;
  private List<String> alergenosEvitar;
  private Integer kcalObjetivo;
  private Boolean incluirBebida; 

}
