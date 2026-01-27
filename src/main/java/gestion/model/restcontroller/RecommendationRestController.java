package gestion.model.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.service.RecommendationService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationRestController {

  @Autowired
  private RecommendationService recommendationService;

  @PostMapping
  public ResponseEntity<?> recomendar(@RequestBody RecommendationRequest req) {
    return ResponseEntity.ok(recommendationService.recomendar(req));
  }
}
