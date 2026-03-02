package gestion.model.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.service.RecommendationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationRestController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<?> recomendar(@RequestBody RecommendationRequest req) {
        return ResponseEntity.ok(recommendationService.recomendar(req));
    }
}